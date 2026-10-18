#!/usr/bin/env bash
# push_model.sh : installs the Vosk Hindi model pack into the debug app's private files (what a pack download does)
A=~/Library/Android/sdk/platform-tools/adb
M=${DEMO_DIR:-/tmp/raahi-demo}/models/vosk-model-small-hi-0.22
$A push $M /data/local/tmp/ >/dev/null
$A shell "run-as org.raahi mkdir -p files/packs && run-as org.raahi cp -r /data/local/tmp/vosk-model-small-hi-0.22 files/packs/"
$A shell "run-as org.raahi ls files/packs/vosk-model-small-hi-0.22"
