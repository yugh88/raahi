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

## Status

**Phase 1 — design system and every screen (static).** All 21 student screens (S1–S21) and
7 teacher screens (T1–T7) run in five languages (Hindi, Tamil, Telugu, Kannada, Malayalam)
on **design-reference data**, labelled as such in the app. Phase 2 replaces that data with
real, sourced records in an encrypted on-phone database.

## Build

Requirements: Android SDK (platform 37 is downloaded on first build), Python 3.11+.
JDK 21 is provisioned automatically through Gradle toolchains.

```sh
make build   # debug + release APKs (one per ABI) and the 25 MB size check
make test    # unit and screenshot tests, all screens × 5 languages
```

Android only: `cd apps/android && ./gradlew :app:installDebug`.

## Repository

```
apps/android     Kotlin, Jetpack Compose app (feature + core modules)
apps/dashboard   Next.js dashboard (Phase 8)
services/core    Java 21, Spring Boot 4.1 (Phase 4)
services/ai      Python, FastAPI grounded AI (Phase 5)
data/            public-data ingestion and hand-checked records (Phase 2)
packs/           content-pack builder and signing (Phase 2)
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
