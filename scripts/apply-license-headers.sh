#!/usr/bin/env bash
# Applies the repo's SPDX license header to all first-party Kotlin sources.
# Idempotent: skips files that already carry the header.
# Usage: bash scripts/apply-license-headers.sh
set -euo pipefail
cd "$(dirname "$0")/.."

HEADER=$(cat <<'EOF'
/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * Retarget — turning advertising's own toolbox toward your goals.
 * Copyright (C) 2026 Jim Spurgeon. For license text see LICENSE.
 */
EOF
)

count=0
while IFS= read -r -d '' file; do
    if ! head -n 5 "$file" | grep -q "SPDX-License-Identifier: AGPL-3.0-or-later"; then
        printf '%s\n%s\n' "$HEADER" "" | cat - "$file" > "$file.tmp" && mv "$file.tmp" "$file"
        count=$((count + 1))
        echo "  + $file"
    fi
done < <(find app/src -type f -name "*.kt" -print0)

echo "Done. Headers added to $count file(s)."
