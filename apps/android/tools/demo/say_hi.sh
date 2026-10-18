#!/usr/bin/env bash
# say_hi.sh "<hindi text>" : makes a 16 kHz mono WAV with the macOS Hindi voice and puts it where the debug build's mic reads it
D=${DEMO_DIR:-/tmp/raahi-demo}/wav
A=~/Library/Android/sdk/platform-tools/adb
say -v Lekha -o $D/u.aiff "$1"
ffmpeg -loglevel error -y -i $D/u.aiff -af "adelay=400,apad=pad_dur=0.8" -ar 16000 -ac 1 -sample_fmt s16 $D/u.wav
$A push $D/u.wav /data/local/tmp/u.wav >/dev/null
$A shell "run-as org.raahi mkdir -p files/debug && run-as org.raahi cp /data/local/tmp/u.wav files/debug/utterance.wav"
