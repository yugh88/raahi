#!/usr/bin/env bash
# Fails if any per-ABI release APK is 25 MB or larger (rule 5). Prints a size table either way.
# Usage: tools/apk-size-check.sh [apk-dir]   (default: app/build/outputs/apk/release)
set -euo pipefail
DIR="${1:-app/build/outputs/apk/release}"
LIMIT=$((25 * 1024 * 1024))
status=0
shopt -s nullglob
apks=("$DIR"/*-release.apk)
if [ ${#apks[@]} -eq 0 ]; then echo "no release APKs in $DIR"; exit 1; fi
printf "%-40s %12s %8s\n" "APK" "bytes" "MB"
for apk in "${apks[@]}"; do
  size=$(wc -c < "$apk" | tr -d ' ')
  printf "%-40s %12s %8.2f\n" "$(basename "$apk")" "$size" "$(echo "$size / 1048576" | bc -l)"
  if [ "$size" -ge "$LIMIT" ]; then echo "  ^ over the 25 MB budget"; status=1; fi
done
exit $status
