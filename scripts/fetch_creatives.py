#!/usr/bin/env python3
"""Dev-side creative pipeline for retarget.

Fetches top Unsplash images per preset theme, recompresses them to
wallpaper-appropriate sizes, and writes per-pack manifests with mandatory
license/photographer metadata. The resulting assets are committed to the
repo and bundled into the APK; the app itself is fully offline.

Guarantees (issue #4):
  - No image ships twice, ever: a shipped-IDs ledger (.creative-ledger.json)
    persists across runs; images already shipped are skipped. Quarterly
    refreshes draw only from never-before-shipped photos.
  - Every image has photographer + license metadata or the pack fails
    validation before writing.

Usage:
    python scripts/fetch_creatives.py --theme fresh-air
    python scripts/fetch_creatives.py --all         # every catalog preset
    python scripts/fetch_creatives.py --validate     # validate packs only

Requires: secrets.properties with UNSPLASH_ACCESS_KEY (see template).
Network is used ONLY by this dev script, never by the app.
"""

import argparse
import hashlib
import json
import io
import os
import re
import shutil
import subprocess
import sys
import urllib.parse
import urllib.request

# Windows consoles default to cp1252; photographer names are international.
# Force UTF-8 on stdout/stderr so a name like "João" can't crash the pipeline.
if sys.stdout.encoding and sys.stdout.encoding.lower() not in ("utf-8", "utf8"):
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", errors="replace")
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding="utf-8", errors="replace")

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS_DIR = os.path.join(REPO_ROOT, "app", "src", "main", "assets", "creative-packs")
LEDGER_PATH = os.path.join(REPO_ROOT, ".creative-ledger.json")
SECRETS_PATH = os.path.join(REPO_ROOT, "secrets.properties")

API_BASE = "https://api.unsplash.com/search/photos"
PHOTO_PAGE = "https://api.unsplash.com/photos/"

# Quality bar: long edge >= 1600 px source minimum, re-encoded to 1440 max.
MIN_SOURCE_LONG_EDGE = 1600
TARGET_LONG_EDGE = 1440
JPEG_QUALITY = 78

# Unsplash search params: rank by relevance, one page at a time.
PER_PAGE = 30


def load_secrets():
    if not os.path.exists(SECRETS_PATH):
        sys.exit("ERROR: secrets.properties not found. Copy secrets.properties.template.")
    key = None
    with open(SECRETS_PATH, encoding="utf-8") as fh:
        for line in fh:
            if line.startswith("UNSPLASH_ACCESS_KEY="):
                key = line.split("=", 1)[1].strip()
    if not key:
        sys.exit("ERROR: UNSPLASH_ACCESS_KEY missing/empty in secrets.properties.")
    return key


def load_ledger():
    """shipped photo IDs -> pack/theme, persisted forever so nothing ships twice."""
    if os.path.exists(LEDGER_PATH):
        with open(LEDGER_PATH, encoding="utf-8") as fh:
            return json.load(fh)
    return {}


def save_ledger(ledger):
    with open(LEDGER_PATH, "w", encoding="utf-8", newline="\n") as fh:
        json.dump(ledger, fh, indent=2, sort_keys=True)
        fh.write("\n")


def api_get(url, key, params=None):
    if params:
        url = url + "?" + urllib.parse.urlencode(params)
    req = urllib.request.Request(url, headers={"Authorization": f"Client-ID {key}"})
    with urllib.request.urlopen(req, timeout=30) as resp:
        return json.loads(resp.read().decode("utf-8"))


def search_photos(key, query, page):
    data = api_get(API_BASE, key, {
        "query": query,
        "per_page": PER_PAGE,
        "page": page,
        "orientation": "portrait",  # wallpaper-first
        "content_filter": "high",
    })
    return data.get("results", [])


def download_file(url, dest):
    req = urllib.request.Request(url, headers={"User-Agent": "retarget-dev-pipeline"})
    with urllib.request.urlopen(req, timeout=60) as resp, open(dest, "wb") as out:
        shutil.copyfileobj(resp, out)


def longest_edge(path):
    """Return (width, height) from a JPEG's SOF markers without Pillow."""
    with open(path, "rb") as fh:
        data = fh.read()
    i = 2
    while i < len(data):
        if data[i] != 0xFF:
            i += 1
            continue
        marker = data[i + 1]
        if marker in (0xC0, 0xC1, 0xC2, 0xC3):  # SOF0/1/2/3
            h = (data[i + 5] << 8) | data[i + 6]
            w = (data[i + 7] << 8) | data[i + 8]
            return w, h
        if marker in (0xD8, 0x01) or 0xD0 <= marker <= 0xD7:
            i += 2
            continue
        seg_len = (data[i + 2] << 8) | data[i + 3]
        i += 2 + seg_len
    raise ValueError(f"No SOF marker found in {path}")


def recompress(src, dest):
    """Resize to TARGET_LONG_EDGE and re-encode. Uses Pillow when available;
    falls back to ffmpeg; refuses to ship un-resized images."""
    try:
        from PIL import Image  # noqa: PLC0415
        with Image.open(src) as img:
            img = img.convert("RGB")
            w, h = img.size
            scale = TARGET_LONG_EDGE / max(w, h)
            if scale < 1:
                img = img.resize((round(w * scale), round(h * scale)), Image.LANCZOS)
            img.save(dest, "JPEG", quality=JPEG_QUALITY, optimize=True)
        return True
    except ImportError:
        pass
    if shutil.which("ffmpeg"):
        subprocess.run([
            "ffmpeg", "-y", "-loglevel", "error", "-i", src,
            "-vf", f"scale='if(gt(iw,ih),min({TARGET_LONG_EDGE},iw),-2)':"
                   f"'if(gt(iw,ih),-2,min({TARGET_LONG_EDGE},ih))'",
            "-q:v", "4", dest,
        ], check=True)
        return True
    print("ERROR: neither Pillow nor ffmpeg available; cannot recompress. "
          "pip install Pillow and retry.", file=sys.stderr)
    return False


def slugify_theme(theme_id):
    return theme_id.replace("-", "_")


def fetch_pack(theme_id, keywords_with_quotas, key, ledger, dry_run=False):
    """Fetch images with per-sub-theme quotas. keywords_with_quotas: list of (term, quota) tuples."""
    pack_dir = os.path.join(ASSETS_DIR, slugify_theme(theme_id))
    os.makedirs(pack_dir, exist_ok=True)
    manifest_path = os.path.join(pack_dir, "manifest.json")

    # Idempotency: existing manifest entries are kept; only missing slots filled.
    manifest = {"packId": theme_id, "images": []}
    if os.path.exists(manifest_path):
        with open(manifest_path, encoding="utf-8") as fh:
            manifest = json.load(fh)
    have = {img["unsplashId"] for img in manifest["images"]}
    shipped = set(ledger.keys())
    target = sum(q for _, q in keywords_with_quotas)
    print(f"[{theme_id}] have {len(have)}, target {target}, shipped-ever {len(shipped)}")

    # Sub-theme quotas drive diversity; each (term, quota) fills its own slot.
    # (Query loop lives in the per-photo section below.)

    added = 0
    for query, quota in keywords_with_quotas:
        sub_added = 0
        page = 1
        attempts = 0
        while sub_added < quota and attempts < 12:
            attempts += 1
            results = search_photos(key, query, page)
            if not results:
                break
            page += 1
            for photo in results:
                if sub_added >= quota:
                    break
                pid = photo["id"]
                if pid in have or pid in shipped:
                    continue
                user = photo.get("user") or {}
                username = user.get("name") or user.get("username")
                if not username:
                    print(f"  skip {pid}: no photographer attribution (pack integrity rule)")
                    continue
                urls = photo.get("urls") or {}
                raw_url = urls.get("raw")
                if not raw_url:
                    continue
                w, h = photo.get("width", 0), photo.get("height", 0)
                if max(w, h) < MIN_SOURCE_LONG_EDGE:
                    continue
                tmp_src = os.path.join(pack_dir, f".tmp_{pid}_src.jpg")
                dest = os.path.join(pack_dir, f"{pid}.jpg")
                if dry_run:
                    print(f"  would fetch {pid} by {username}")
                    sub_added += 1
                    continue
                # Request the pre-sized variant: imgix params on the raw URL.
                sized = f"{raw_url}&w={TARGET_LONG_EDGE}&q={JPEG_QUALITY}&fm=jpg&fit=max"
                try:
                    download_file(sized, dest)
                except Exception as exc:  # noqa: BLE001
                    print(f"  skip {pid}: download failed ({exc})")
                    continue
                # Verify dimensions from actual bytes; trust nothing from the API.
                try:
                    rw, rh = longest_edge(dest)
                except Exception:
                    os.remove(dest)
                    continue
                if max(rw, rh) < TARGET_LONG_EDGE * 0.9:
                    os.remove(dest)
                    continue
                file_hash = hashlib.sha256(open(dest, "rb").read()).hexdigest()
                manifest["images"].append({
                    "unsplashId": pid,
                    "file": f"{pid}.jpg",
                    "subTheme": query,
                    "photographer": username,
                    "photographerUrl": f"https://unsplash.com/@{user.get('username', '')}",
                    "license": photo.get("links", {}).get("license", "Unsplash License"),
                    "licenseUrl": "https://unsplash.com/license",
                    "sourceUrl": urls.get("html", f"https://unsplash.com/photos/{pid}"),
                    "width": rw,
                    "height": rh,
                    "sha256": file_hash,
                })
                ledger[pid] = theme_id
                sub_added += 1
                added += 1
                print(f"  fetched {pid} [{query}] ({rw}x{rh}) by {username}")

    if not dry_run and added:
        manifest["images"].sort(key=lambda im: im["unsplashId"])
        with open(manifest_path, "w", encoding="utf-8", newline="\n") as fh:
            json.dump(manifest, fh, indent=2)
            fh.write("\n")  # trailing newline for POSIX-friendly diffs
        save_ledger(ledger)
    print(f"[{theme_id}] done: {added} added, pack now {len(manifest['images'])}")
    return added


def validate_packs(expected_counts=None):
    """Standalone validation, mirroring checkCreativeLicenses in CI."""
    errors = []
    if not os.path.isdir(ASSETS_DIR):
        sys.exit("No creative-packs directory; nothing to validate.")
    seen_ids = set()
    for theme in sorted(os.listdir(ASSETS_DIR)):
        pack_dir = os.path.join(ASSETS_DIR, theme)
        if not os.path.isdir(pack_dir):
            continue
        manifest_path = os.path.join(pack_dir, "manifest.json")
        if not os.path.exists(manifest_path):
            errors.append(f"{theme}: manifest.json missing")
            continue
        with open(manifest_path, encoding="utf-8") as fh:
            manifest = json.load(fh)
        for img in manifest.get("images", []):
            fid = img.get("unsplashId")
            for field in ("unsplashId", "file", "photographer", "license", "licenseUrl", "sha256"):
                if not img.get(field):
                    errors.append(f"{theme}/{fid}: missing required field '{field}'")
            if fid in seen_ids:
                errors.append(f"{theme}/{fid}: duplicate Unsplash ID across packs")
            seen_ids.add(fid)
            file_path = os.path.join(pack_dir, img.get("file", ""))
            if not os.path.isfile(file_path):
                errors.append(f"{theme}/{fid}: referenced file missing on disk")
        if expected_counts and theme in expected_counts:
            n = len(manifest.get("images", []))
            if n < expected_counts[theme]:
                errors.append(f"{theme}: {n} images, expected >= {expected_counts[theme]}")
    # Cross-check the ledger: nothing in packs may be missing from it.
    if os.path.exists(LEDGER_PATH):
        ledger = load_ledger()
        for fid in seen_ids:
            if fid not in ledger:
                errors.append(f"ledger missing shipped image {fid} (never-ship-twice rule)")
    return errors


def main():
    ap = argparse.ArgumentParser(description=__doc__)
    g = ap.add_mutually_exclusive_group(required=True)
    g.add_argument("--theme", help="catalog preset id, e.g. fresh-air")
    g.add_argument("--all", action="store_true", help="fetch for all catalog presets")
    g.add_argument("--validate", action="store_true", help="validate packs, no network")
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    if args.validate:
        errs = validate_packs()
        if errs:
            print("VALIDATION FAILED:")
            for e in errs:
                print(f"  - {e}")
            sys.exit(1)
        print("Validation passed.")
        return

    # Per-sub-theme quotas replace flat keyword lists: each (search term, quota)
    # pair guarantees its slice of the pack. Terms target high-potency imagery
    # archetypes per docs/research/imagery-domains.md (awe + soft fascination,
    # down-regulation rather than adrenaline). Target 60+/pack: quotas sum
    # slightly above 60 for fresh-air because sub-theme diversity beats a
    # rigid count; undershoots on availability are acceptable.
    # (forgetting-curve rationale in docs/research/imagery-domains.md).
    THEME_QUOTAS = {
        "hydration": [
            ("sparkling water pour glass", 12),
            ("mountain stream waterfall", 10),
            ("ocean wave splash", 10),
            ("morning dew drop macro", 9),
            ("blue texture water ripple", 9),
            ("citrus infused water", 10),
        ],
        "fresh-air": [
            ("misty mountain peaks layers", 7),
            ("golden hour meadow grass backlight", 6),
            ("calm lake reflection dawn", 6),
            ("wispy clouds open sky", 5),
            ("coastal cliff ocean breeze", 6),
            ("forest path dappled light", 6),
            ("macro dew leaf morning", 5),
            ("desert dune minimal", 5),
            ("starry night sky stars", 5),
            ("snowy summit ridge", 5),
            ("waterfall moss grotto", 5),
            ("rolling hills pasture fog", 5),
        ],
        "fruit": [
            ("ripe peaches close-up", 10),
            ("berry macro water droplets", 10),
            ("citrus slices vibrant", 10),
            ("watermelon splash summer", 8),
            ("orchard harvest sunlight", 10),
            ("dragon fruit exotic macro", 12),
        ],
        "vegetables": [
            ("farmers market vegetable stall", 10),
            ("heirloom tomato close-up", 10),
            ("garden harvest basket", 10),
            ("leafy greens texture macro", 8),
            ("market peppers rainbow", 10),
            ("artisan salad bowl", 12),
        ],
    }

    themes = list(THEME_QUOTAS.keys()) if args.all else [args.theme]
    if args.theme and args.theme not in THEME_QUOTAS:
        sys.exit(f"Unknown theme '{args.theme}'. Known: {', '.join(THEME_QUOTAS)}")

    key = load_secrets()
    ledger = load_ledger()
    for theme in themes:
        fetch_pack(theme, THEME_QUOTAS[theme], key, ledger, args.dry_run)
    errs = validate_packs()
    if errs:
        print("POST-FETCH VALIDATION FAILED:")
        for e in errs:
            print(f"  - {e}")
        sys.exit(1)
    print("All packs valid.")


if __name__ == "__main__":
    main()
