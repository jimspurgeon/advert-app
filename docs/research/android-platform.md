# Android Platform Capabilities & Constraints

Technical research for the app's delivery channels. Versions/behaviors verified
against Android developer documentation (links inline). Update this file when
targetSdk requirements change.

## Delivery Channels

### 1. Wallpaper rotation (`WallpaperManager`)

- `WallpaperManager.getInstance(context)` → `setBitmap(bitmap)` /
  `setStream(stream)`; legacy normal permission `android.permission.SET_WALLPAPER`
  (normal protection level — granted at install, no runtime prompt).
- Android 8+ scope: apps can set *system* wallpaper; lock-screen wallpaper setting
  is generally OEM-dependent and not universally settable via public API
  (`WallpaperManager.FLAG_LOCK` exists but support is inconsistent — many devices
  fall back or throw). Test matrix needed across OEMs (Samsung, Pixel, Xiaomi...).
- Modern Android 13+ honors monochrome/themed icons and wallpaper-based dynamic
  color (Material You). Our wallpaper changes will recolor the system UI subtly —
  an intended, delightful effect that also resets "novelty" of the environment.
- Battery/limits: wallpaper set is cheap (one-time IPC + bitmap decode). Do it in
  a `WorkManager` worker, not the main thread. Bitmaps decoded at
  `suggestDesiredMinimumWidth/Height` to avoid memory churn.
- User control: provide a "revert to original wallpaper" option — snapshot the
  pre-existing wallpaper hash/path (not necessarily the bitmap, to save space)
  before first change so users can restore.

### 2. Notifications (image-led "ad-style" nudges)

- Use `NotificationChannel`s per goal + per urgency tier. Importance defaults:
  `IMPORTANCE_DEFAULT` (no heads-up) for standard nudges; heads-up only for
  user-configured "important moments".
- `setStyle(BigPictureStyle)` for image-rich notifications; `BigTextStyle` fallback.
- Android 13+ requires `POST_NOTIFICATIONS` **runtime** permission — must be
  requested in-context with a rationale (our onboarding explains "brand campaign
  for your goals" concept before asking).
- Full-screen intents / overlays: only with user opt-in and for genuine
  breathing-exercise launches; keep `USE_FULL_SCREEN_INTENT` declarations
  justified in Play policy terms (we're open-source sideload-first, but keep
  Play-friendly posture).
- Notification actions: check-in button ("Logged ✓" / "Snooze 2h" / "Fewer of
  these") directly from the shade — reduces friction to log, collects pacing
  feedback implicitly.

### 3. Exact alarms (Android 12+ restrictions)

- `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` — the goal engine *does not* need
  exact alarms. Approximate windows with jitter are *better* for the nudge
  philosophy (avoid mechanical predictability wearing out). Use WorkManager
  periodic + flex windows; AlarmManager only as fallback for wake-locked timebox
  scenarios (rare).
- Doze/App Standby: WorkManager persists across reboots and handles Doze-appropriate
  deferral. Accept inexactness; never fight the OS for punctuality. An ad-like
  ambient presence doesn't need second-level precision — it needs reliability over
  months. This is also battery-polite behavior users will trust.

### 4. Widgets & glanceability

- Glance widgets (Compose for Widgets) for home-screen goal cards with imagery,
  refreshed at controlled cadences. Widget refresh is OS-bounded (update
  broadcast), so refresh cadence comes from WorkManager enqueue schedule.
- Interactive "check-in" from widget via `WorkManager`+broadcast (no Service
  needed).

### 5. Dynamic Color & Material You integration

- M3 dynamic color schemes derived from wallpaper — our wallpaper rotation thus
  re-themes the whole system + app ecosystem tonally. Free "environment makeover"
  effect amplifying mere exposure novelty. Leverage in-app: preview how the
  next wallpaper will tint system UI before applying.

### 6. Lock-screen & Always-On ambient surfaces

- Lock-screen wallpaper API inconsistency noted above; keyguard widgets are gone;
  consider notification-ledge "ambient mode" alternatives: persistent low-
  priority notification "goal ticker" (custom views, non-alerting channel)
  acting as an always-available glance surface.

### 7. Live Wallpapers (deferred)

- A `WallpaperService` live wallpaper with Ken Burns-style slow pans over nature
  imagery would be gorgeous and on-theme (ambient exposure) — but battery + OEM
  variance + maintenance cost put it in Phase 3+. Document for later; static
  first, live later maybe.

### 8. Privacy architecture on Android

- Room DB local-only; no network permission in v1 manifest at all (strongest
  possible privacy posture: app cannot phone home). Network capability, if ever
  added for optional remote image packs, arrives as a separate module with its
  own manifest `INTERNET` permission so F-Droid-style permission audits show v1
  build has zero network perms.
