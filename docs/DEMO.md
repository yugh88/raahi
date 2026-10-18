# Raahi — demo guide

This file grows with each phase. The final 5-minute script (Phase 9) runs the same flow twice:
once in **Hindi, offline**, and once in **Tamil on throttled 2G** (Add-on A, rule 9).

---

## Phase 1 — design system and every screen (static)

**What it shows:** the whole app in five languages, on a low-end phone, in airplane mode.
All 21 student screens (S1–S21) and 7 teacher screens (T1–T7) are built and connected.
Every value on screen is **design-reference data** (from `docs/ui-reference.html` or public
facts) and the app says so in a gold strip at the top. Real, sourced data arrives in Phase 2.

**Not in Phase 1 (by plan):** voice (mic, Didi speaking, tap-to-hear audio) is Phase 3;
the mic and keyboard buttons are present but silent. Nothing is saved between launches yet
(Room + SQLCipher is Phase 2).

### Set up the phone

Any Android 8+ phone, or the emulator profile used for testing:

```sh
# 2 GB RAM, 720×1440 at 320 dpi (= 360×720 dp, the size of a ₹6,000–8,000 phone)
avdmanager create avd -n Raahi_LowEnd_2GB \
  -k "system-images;android-35;google_apis_playstore;arm64-v8a" -d pixel
# then in ~/.android/avd/Raahi_LowEnd_2GB.avd/config.ini:
#   hw.ramSize=2048  hw.lcd.width=720  hw.lcd.height=1440  hw.lcd.density=320  hw.cpu.ncore=2
emulator -avd Raahi_LowEnd_2GB
```

Install and go offline:

```sh
cd apps/android
./gradlew :app:installDebug            # debug build: has the screen index
adb shell cmd connectivity airplane-mode enable
```

The release build (`./gradlew :app:assembleRelease`, then install
`app/build/outputs/apk/release/app-arm64-v8a-release.apk`) has no screen index.

### Click-through (≈ 4 minutes)

| # | Do | See |
|---|---|---|
| 1 | Open the app | **S1**: Didi waving, five language tiles, each in its own script |
| 2 | Tap **தமிழ்** | Whole app switches to Tamil at once, no restart |
| 3 | Next → pick a class → Next → Next → pick an interest → Next → pick a goal → Next | **S2**: five questions, one per screen, dots show progress |
| 4 | | **S3 Home**: greeting, streak, offline line, exactly 3 big cards, Didi + mic, test NGO session card |
| 5 | Tap the avatar (top right) | **S7 Progress** (not a tab); back |
| 5b | Tap the mic next to Didi | **S5 Ask Didi**: answer with a source line, "listen again", "document list" |
| 6 | Tap **today's lesson** card | **S4**: picture, question, 3 options; pick the closed circuit → leaf + "+10 diyas" |
| 7 | **सीखो** tab | **S9 Career mountain**: summit (job, NAPS, scholarship) → locked → current with climber → done |
| 8 | Scroll, **Start** | **S10** trap test (no score) → tap an answer → **S11** "stuck in 3 places" |
| 9 | **Watch that part of the video** | **S12** segment player, transcript, speed 0.75/1/1.25 |
| 10 | **Try again** | **S13** cleared: climber moves up, badge, "Didi will ask again in 3 days" |
| 11 | **Courses** (top of S9) | **S14** (NCERT via DIKSHA, verified; teacher courses empty) → **S15** exam plan → **S16** pay-after-you-earn (18+ only) |
| 12 | **कमाओ** tab | **S6**: 3 matched cards (no mentor card), scheme check, documents, "fill the form with Didi" |
| 13 | **Which schemes am I eligible for?** → pick an income band | **S19** → **S20** eligible / maybe / not, each with reason and source |
| 14 | **How to apply** | **S21**: one step at a time, safe draft fields with Copy, open official form, never-store warning |
| 15 | Back to **S6** → **Documents ready?** | **S18**: have / missing / expiring, where to get each |
| 16 | **Home** → NGO session card | **S17**: labelled test event, date and distance "not available" |
| 17 | Debug only: tap the gold strip → **S8** | **Teacher**: learners on a shared phone, Didi alert → **T1** heatmap → **T2** assign → "saved, will send when network comes" |
| 18 | From S8 | **T3** studio → **T4** pricing; **T5** earnings (empty); **T6** planner (offline: queued); **T7** recommend (consent note) |

Exactly three bottom tabs everywhere: घर · सीखो · कमाओ.

### Checks done for this phase (2026-10-18)

- **Offline:** the app requests no permissions at all (not even INTERNET), so it cannot
  touch the network; the whole click-through above was run in airplane mode on the
  2 GB emulator. The 50 kbps test becomes meaningful from Phase 4 (first network use).
- **APK size:** release, per ABI — arm64-v8a 2.84 MB, armeabi-v7a 2.84 MB (budget 25 MB).
  `tools/apk-size-check.sh` enforces it in CI.
- **Cold start** (release, 2 GB emulator, `am start -W`):
  - first launch straight after sideloading, before Android compiles the app: 2.1–3.6 s;
  - after Android's background compile of the bundled library profiles: 0.56–1.8 s;
  - fully compiled: 0.47–0.93 s.
  The first-launch number is over the 3 s target. An app-specific Baseline Profile
  (Phase 9) addresses it; a real 2 GB phone measurement is still needed.
- **Screenshots:** 165 images (33 screens and states × 5 languages) under each module's
  `src/test/screenshots/`; `make test` verifies them.
- **Taps:** a regression test checks that tapping any text inside a tile or card triggers
  the tile (found on the device: sublabels were taking the tap).

### Commands

```sh
make build        # debug + release, size check
make test         # 174 tests incl. screenshots in 5 languages
make screenshots  # re-record after an intended UI change, then review the PNG diff
```
