#!/usr/bin/env bash
# tap.sh "<text>" [screenshot-name] : taps the first UI node whose text starts with <text>, optionally saves a screenshot
A=~/Library/Android/sdk/platform-tools/adb
SS=${DEMO_DIR:-/tmp/raahi-demo}/device
$A shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1
xy=$($A shell cat /sdcard/ui.xml | python3 -c "
import sys,re
t=sys.argv[1]; x=sys.stdin.read()
ms=list(re.finditer(r'(?:text|content-desc)=\"([^\"]*)\"[^>]*?bounds=\"\[(\d+),(\d+)\]\[(\d+),(\d+)\]\"',x))
for m in sorted(ms,key=lambda m: m.group(1)!=t):
    if m.group(1).startswith(t):
        a,b,c,d=map(int,m.group(2,3,4,5)); print((a+c)//2,(b+d)//2); break
" "$1")
if [ -z "$xy" ]; then echo "NOT FOUND: $1"; exit 1; fi
$A shell input tap $xy; sleep 1.2
if [ -n "$2" ]; then $A exec-out screencap -p > "$SS/$2.png"; fi
echo "tapped $1 at $xy"
