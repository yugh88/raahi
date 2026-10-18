#!/usr/bin/env bash
# rec.sh start <name> | stop <name> : record the emulator screen for one demo segment
A=~/Library/Android/sdk/platform-tools/adb
V=${DEMO_DIR:-/tmp/raahi-demo}/video
case "$1" in
  start) $A shell rm -f /sdcard/$2.mp4; ($A shell screenrecord --bit-rate 6000000 --time-limit 180 /sdcard/$2.mp4 >/dev/null 2>&1 &); sleep 1.5 ;;
  stop) sleep 1.5; $A shell pkill -INT screenrecord; sleep 2; $A pull /sdcard/$2.mp4 $V/$2.mp4 >/dev/null && echo "saved $2" ;;
esac
