# Raahi (राही)

A voice-first, offline-first Android app that walks rural students (Class 9 to graduation) from
**discovering** a career, to **climbing** its roadmap, to **learning** without fear, to
**earning** through real scholarships, schemes, apprenticeships and jobs. Didi, a helper
character, speaks every screen. Built for slow 2G and patchy signal; every student screen
also works in airplane mode.

Entry for the Citi India Hackathon problem *"Reimagining Education & Economic Empowerment for
Rural India"*.

- Design: [`docs/ui-reference.html`](docs/ui-reference.html)
- Architecture: [`docs/raahi-architecture.png`](docs/raahi-architecture.png)
- Build plan, versions, risks, decisions: [`docs/PLAN.md`](docs/PLAN.md)
- How to demo the current phase: [`docs/DEMO.md`](docs/DEMO.md)

## Demo

**[Watch the 4-minute demo](docs/demo/raahi-demo.mp4)**. It was recorded in airplane mode on a 2 GB emulator and has captions but no sound. The full click-through script is in [`docs/DEMO.md`](docs/DEMO.md).

## Status (hackathon submission, 19 Oct 2026)

**Working on the phone, offline, in Hindi:**

| | |
|---|---|
| Learning loop | Class 10 Science *Electricity*: lesson → trap test (no score) → traps found → Didi explains → retry with a different question → trap cleared → spaced re-check (1/3/7/21 days) → the climber moves up the mountain |
| Scheme finder | At most 6 adaptive questions → eligible / maybe / not for **10 real schemes**. Every reason quotes the official rule, with source and date checked ([sources](docs/DATA_SOURCES.md)) → step-by-step guide with the official link and safe draft fields → "I have applied" tracking → documents checklist |
| Ask Didi | Answers only from the curated lesson and scheme records, always with the source. Otherwise she says she doesn't know |
| Voice | Hindi speech recognition on the phone (Vosk, per-screen grammar, rule-based matching), Didi speaks through the phone's offline Hindi voice, and tap-to-hear on every text. Picture tiles always work too |
| Privacy | SQLCipher database with a random key wrapped by Android Keystore. Income and caste are kept as bands on the phone only. The app has **no INTERNET permission**; its only permission is the microphone |
| Quality | 200+ tests, including engine tests on the real rules and screenshot tests of every screen in 5 languages. Release APK is 14.9 MB (arm64) / 12.9 MB (armv7) against a 25 MB budget |

**Not built yet** (labelled "design reference" in the app): backend, login and sync; the AI service; teacher dashboard and tools; courses and pay-after-you-earn; NGO events. Also missing:
- scheme rules for Andhra Pradesh and Telangana (their official pages could not be read);
- a lesson video;
- lesson text in ta/te/kn/ml (the interface is translated; translations await native-speaker review).

See [`docs/ROADMAP.md`](docs/ROADMAP.md).

## Build

Requirements: Android SDK (platform 37 is downloaded on first build), Python 3.11+.
JDK 21 is provisioned automatically through Gradle toolchains.

```sh
make build   # debug + release APKs (one per ABI) and the 25 MB size check
make test    # engine tests on real rules, unit and screenshot tests (all screens × 5 languages)
```

Android only: `cd apps/android && ./gradlew :app:installDebug`.

Offline voice needs the Vosk Hindi model pack (42 MB, not in the APK). Download
[vosk-model-small-hi-0.22](https://alphacephei.com/vosk/models/vosk-model-small-hi-0.22.zip),
unzip it, and install it into a debug build with `apps/android/tools/demo/push_model.sh`
(set `DEMO_DIR` to the folder that contains `models/`). Without the model, every screen
still works by tapping pictures.

## Repository

```
apps/android     Kotlin, Jetpack Compose app (feature + core modules)
apps/dashboard   Next.js dashboard (Phase 8)
services/core    Java 21, Spring Boot 4.1 (Phase 4)
services/ai      Python, FastAPI grounded AI (Phase 5)
data/curated     hand-checked schemes, eligibility questions and the Electricity lesson
packs/           builds the bundled content pack (manifest + SHA-256 per file)
infra/           docker-compose for local services (Phase 4)
docs/            design, plan, demo script, legal notes
```

## Principles

- **Low bandwidth first.** Screens read only from the phone's database; the network is used
  to sync, never to render.
- **Real data only.** No synthetic records, users or numbers anywhere. Every knowledge record
  carries `source_url` and `fetched_at`; unknown values show "not available".
- **Privacy.** No ads, no tracking SDKs. Aadhaar and bank numbers are never stored; income
  and category stay on the phone.

## Licence

Apache-2.0 (see [`LICENSE`](LICENSE)). Bundled fonts are under the SIL Open Font License; see
[`NOTICE`](NOTICE).
