# Raahi — engineering plan

First written 2026-10-18 (Prompt 0), updated the same day with the owner's decisions and
Add-on A (five languages). Library versions were read from Maven Central, Google Maven, npm,
PyPI and Docker Hub on that date (latest stable, no alphas/RCs/milestones).

## 1. Repo tree

```
raahi/
├─ LICENSE (Apache-2.0) · NOTICE · README.md · CONTRIBUTING.md
├─ Makefile                    `make build` / `make test` across all stacks
├─ .github/workflows/          ci.yml (builds, tests, APK size gate), deps-scan.yml (Phase 9)
├─ apps/
│  ├─ android/                 own Gradle build
│  │  ├─ settings.gradle.kts · build.gradle.kts · gradle/libs.versions.toml · gradlew
│  │  ├─ build-logic/convention/   raahi.android.library, raahi.android.feature,
│  │  │                            raahi.android.compose, raahi.hilt, raahi.roborazzi,
│  │  │                            raahi.jvm.library
│  │  ├─ i18n/                 one TSV per module: key · en · hi · ta · te · kn · ml · reviewed
│  │  │                        + generate.py → values*/strings.xml (CI checks it is current)
│  │  ├─ app/                  Application, MainActivity, NavHost, 3-tab scaffold, Hilt root
│  │  ├─ core/
│  │  │  ├─ model/             pure-Kotlin domain types shared by engine + data
│  │  │  ├─ designsystem/      tokens, per-script fonts, Didi vectors, icons, components,
│  │  │  │                     AppLanguage + LocalSpeaker + LocalReadingLevel
│  │  │  ├─ data/              Room + SQLCipher, DAOs, repositories, pack importer
│  │  │  ├─ sync/              outbox, WorkManager workers, delta pull, pinned OkHttp
│  │  │  ├─ security/          Keystore key wrap, Tink Ed25519 verify, token store
│  │  │  ├─ voice/             Vosk (hi, te), Bhashini STT client (ta, kn, ml), per-screen
│  │  │  │                     grammars, IntentRouter, TTS for 5 locales
│  │  │  ├─ engine/            pure Kotlin (JVM): TrapEngine, SpacedRepetition,
│  │  │  │                     EligibilityEngine, SchemeQuestionSelector,
│  │  │  │                     VerdictExplainer, CareerScorer, RoadmapBuilder,
│  │  │  │                     AnswerSearch, ReadingLevelEstimator
│  │  │  ├─ media/             Media3 segment player, transcript sync
│  │  │  └─ testing/           real-record fixture loader, screenshot test helpers
│  │  ├─ feature/
│  │  │  ├─ onboarding/  S1 S2          ├─ askdidi/   S5
│  │  │  ├─ home/        S3             ├─ earn/      S6 S18–S21
│  │  │  ├─ learn/       S4 S10–S13     ├─ progress/  S7
│  │  │  ├─ roadmap/     S9             ├─ events/    S17
│  │  │  ├─ courses/     S14 S15 S16    └─ teacher/   S8 T1–T7
│  │  └─ baselineprofile/  (Phase 9)
│  └─ dashboard/               Next.js App Router + TypeScript
├─ services/
│  ├─ core/                    Java 21, Spring Boot 4.1, Gradle (Kotlin DSL)
│  │  └─ org.raahi.core.{profile,content,course,payment,sync,partner,event,opportunity,
│  │                      teacher,audit,security,storage}
│  │     storage = S3-compatible interface; dev impl serves packs from local disk
│  ├─ ai/                      Python, FastAPI
│  │  └─ raahi_ai/{api,providers,rag,safety,cache,planner,speech(Bhashini),translate}
│  │     eval/ (50 FAQ-based questions + 10 native-speaker questions per language)
│  └─ keycloak-otp/            Keycloak authenticator SPI: phone OTP (dev stub logs OTP)
├─ data/
│  ├─ ingest/                  one script per source → normalized JSON/CSV
│  ├─ curated/                 hand-checked records, each with source_url + fetched_at
│  │  └─ schemes/{central,mp,tn,ap,ts,ka,kl}/ · careers/ · lessons/ · glossary/
│  └─ schema/                  JSON Schemas every curated file is validated against
├─ packs/                      pack builder + Ed25519 sign/verify CLI (Python)
│                              pack kinds: content, language, speech-model, scheme-set
├─ infra/                      docker-compose.yml, keycloak realm export, k8s/ (later)
└─ docs/                       kit files, PLAN.md, DEMO.md, LEGAL_NOTES.md,
                               TRANSLATION_REVIEW.md, ROADMAP.md, SCALE.md,
                               USABILITY_TEST.md
```

## 2. Exact versions

| Area | Library | Version |
|---|---|---|
| Android build | Android Gradle Plugin | 9.4.1 |
| | Gradle wrapper | 9.8.1 |
| | Kotlin | 2.4.21 |
| | KSP | 2.3.12 |
| | compileSdk / targetSdk / minSdk | 36 / 36 / 26 |
| | ABIs | arm64-v8a, armeabi-v7a (per-ABI APKs + AAB) |
| Android UI | Compose BOM | 2026.09.00 |
| | activity-compose | 1.13.0 |
| | lifecycle (runtime-compose, viewmodel) | 2.11.0 |
| | navigation-compose | 2.10.2 |
| DI | Hilt | 2.60.1 (hilt-navigation-compose 1.4.0) |
| Data | Room | 2.8.5 |
| | net.zetetic:sqlcipher-android | 4.19.1 |
| | WorkManager | 2.12.0 |
| | kotlinx-serialization-json / coroutines | 1.11.0 / 1.11.0 |
| Security | Google Tink (Ed25519 verify) | latest stable, pinned in Phase 2 |
| Network | OkHttp | 5.5.0 |
| Media | Media3 ExoPlayer | 1.11.1 |
| Voice | com.alphacephei:vosk-android | 0.3.75 |
| | vosk-model-small-hi-0.22 | 44,458,845 bytes (checked) |
| | vosk-model-small-te-0.42 | 60,544,249 bytes (checked) |
| Mesh | play-services-nearby | 19.5.1 |
| QR | com.google.zxing:core | 3.5.4 |
| Tests | Roborazzi / Robolectric | 1.76.0 / 4.17 |
| Perf | profileinstaller / benchmark-macro | 1.4.1 / 1.5.0 |
| Core service | Java (Gradle toolchain) | 21 |
| | Spring Boot | 4.1.1 |
| | springdoc-openapi | 3.1.1 |
| | Flyway | managed by Boot BOM |
| Infra | pgvector/pgvector | 0.8.7-pg16 |
| | Redis | 8.8.3 |
| | Keycloak | 26.8.1 |
| | Redpanda | v26.2.5 |
| | Object storage | dev: local disk behind S3 interface; options in Phase 4 |
| AI service | Python | 3.12 |
| | FastAPI / uvicorn / pydantic | 0.143.5 / 0.54.0 / 2.14.1 |
| | sentence-transformers | 6.1.0 (multilingual-e5-small) |
| | psycopg / pgvector (py) / redis-py / httpx | 3.3.6 / 0.5.1 / 8.1.0 / 0.28.1 |
| Dashboard | Node | 22 LTS |
| | Next.js / React | 16.4.2 / 19.3.1 |
| | TypeScript | 5.9.3 |
| | Recharts | 3.10.1 |

LLM calls use plain httpx against the OpenAI-compatible chat API, so Groq, vLLM and Ollama differ
only by base URL + model name. No LangChain/LlamaIndex.

### Fonts (Google Fonts, SIL OFL 1.1, bundled)

Subset to the script + Basic Latin + ₹ + Indic joiners, all OpenType layout features kept,
width axis pinned to 100 on Noto. All twelve files together are ~1.5 MB compressed, so every
script ships inside the APK (works offline from install). A language pack may later ship a
newer font file, which overrides the bundled one.

| Script | UI (titles, tiles) | Body / sublabels |
|---|---|---|
| Devanagari (hi) | Baloo 2 (variable wght) | Mukta 400/500/600 |
| Tamil (ta) | Baloo Thambi 2 | Noto Sans Tamil |
| Telugu (te) | Baloo Tammudu 2 | Noto Sans Telugu |
| Kannada (kn) | Baloo Tamma 2 | Noto Sans Kannada |
| Malayalam (ml) | Baloo Chettan 2 | Noto Sans Malayalam |
| English sublabels | — | Mukta (Latin) |

## 3. Decisions (2026-10-18)

- Spring Boot 4.1.1 (3.5 OSS support ended 2026-06-30).
- LLM gpt-oss-20b behind a provider interface; intent = Vosk grammar + rules, no trained model.
- S7 opens from the avatar at top right; exactly 3 bottom tabs.
- Thinking ring = didiOrange #F5A524; gold #A86F00 only for text and pills.
- Reference example values only in Phase 1 previews/screenshot tests, labelled
  "design reference". Removed entirely: "Sunita", "Rampur #2 of 12", "₹1,240 earned".
  From Phase 2 every value is a real record with source_url or shows "not available".
  Mentor cards only for real onboarded mentors with consent (none in the demo by default).
- Per-ABI APKs for arm64-v8a and armeabi-v7a, each < 25 MB; CI fails above that.
- SQLCipher raw 256-bit key wrapped by Android Keystore (no PBKDF2).
- FTS5 must be verified in sqlcipher-android; fallback FTS4 + tell the owner.
- Tink for Ed25519 verification on API 26+.
- Keycloak phone-OTP SPI; dev stub logs OTP; never enabled in prod config.
- Dev object storage = local disk behind an S3-compatible interface.
- Pay-after-you-earn: 18+ only, fixed capped fee, no interest/credit/lending/penalties,
  sandbox payments; under-18s get free or sponsor-paid courses with guardian informed.
  See LEGAL_NOTES.md.
- JDK 21 via Gradle toolchains; Node 22 LTS.

## 4. Add-on A — five languages: what changes

**Languages:** hi, ta, te, kn, ml (+ English sublabels everywhere).

**Strings.** One TSV per Android module in `apps/android/i18n/` is the single source
(key, en, hi, ta, te, kn, ml, reviewed-by, reviewed-on). `generate.py` writes
`values/` (English base), `values-hi`, `values-ta`, `values-te`, `values-kn`, `values-ml`.
English sublabels are read through an English-locale `Resources`, so each label has one key.
The ta/te/kn/ml UI strings written during Phase 1 are drafts and are listed as **unreviewed**
in `docs/TRANSLATION_REVIEW.md` until a native speaker signs off (rule: never ship
unreviewed machine translations; the same gate applies to UI strings).
Adding a language with no code change: Phase 2 adds a `language` pack kind carrying a
string table that overrides resources at runtime, plus its font, glossary, prompts and
scheme set; the S1 tile list is read from installed/available language packs.

**Language pick.** S1 shows the five tiles first. The app applies the language in-process
(localized Context provided to Compose), so no AppCompat dependency and no restart.

**Layout.** Every component wraps; key labels never ellipsize. Screenshot tests render all
screens in all five languages (25 screens × 5 = 125 images) at a small-phone size
(360 × 720 dp) so Tamil/Malayalam length problems show up in review.

**Voice (Phase 3).**

| Lang | STT offline | STT online | TTS |
|---|---|---|---|
| hi | Vosk small-hi-0.22 | same | Android hi-IN |
| te | Vosk small-te-0.42 if measured accuracy is acceptable | Bhashini | Android te-IN |
| ta, kn, ml | none → picture + tap | Bhashini via services/ai, ≤ 5 s Opus 16 kbps | Android ta/kn/ml-IN |

Missing TTS voice → one-time voice-data install prompt on Wi-Fi; pre-recorded Didi prompts
from the language pack meanwhile. Accuracy is measured on public test sets
(Common Voice, Kathbath), not on made-up audio.

**Data budget addition.** Ask Didi by voice in ta/kn/ml uploads ≤ 5 s of Opus at 16 kbps
= ≤ 10 KB per question (plus ~2 KB text) — still well under a typical voice bot, but it is
audio and is disclosed in the data-budget table and in-app. hi and te stay text-only.

**Content (Phase 2).** Electricity lessons per language from openly licensed state-board /
DIKSHA material where it exists; otherwise Bhashini translation + named native reviewer and
date in the pack manifest. Unreviewed translations never ship.

**Glossary.** Per-language technical-term glossary in each language pack
(e.g. प्रतिरोध / மின்தடை / నిరోధం / ರೋಧ / പ്രതിരോധം = resistance), used by lessons,
AnswerSearch query expansion and /ask bracketed English terms.

**Schemes.** ~50 real records: central + MP, TN, AP/Telangana, Karnataka, Kerala, each with
source_url + fetched_at from the official page.

**AI.** /ask answers in the student's language with key English terms in brackets.
Eval: 10 native-speaker questions per language → grounded-answer rate per language. If
gpt-oss-20b is weak in a language: generate in English from the same sources → Bhashini
translate → flag for review. No answer is ever shown without its source.

**Reading level.** Profile.readingLevel estimated from onboarding + tap-to-hear frequency
(ReadingLevelEstimator in :core:engine). Low = auto-speak, pictures first, sublabels hidden.
Phase 1 adds the `LocalReadingLevel` hook in the design system.

**Demo.** docs/DEMO.md runs onboard → mountain → trap loop → scholarship match → Ask Didi
once in Hindi offline and once in Tamil on throttled 2G.

## 4b. Add-on B — voice scheme finder and assisted enrollment: what changes

**Screens (in :feature:earn).** S19 Scheme check (Didi asks ≤ 6 questions, voice or picture
tiles), S20 Verdict (eligible / maybe / not eligible, each with reason, source and
"checked on" date, read aloud), S21 Enrollment guide (one step at a time, "हो गया" or tap
advances; assisted-apply draft with copy buttons and a deep link to the official form;
teacher-assisted mode; nearest CSC or "ask your teacher"). S6 gets a "मैं किसके लिए योग्य?"
entry to S19, and each S6 card opens S21. Phase 1 builds them static like the rest.

**Engine (Phase 2, pure Kotlin, unit-tested on real rules from data/curated).**
- `SchemeQuestionSelector`: candidates = schemes whose rules are not yet decided. For each
  unanswered profile field, compute the expected entropy of the eligible/not split across
  candidates after asking it (answer distribution = the field's possible bands); ask the
  field with the highest information gain; stop at 6 questions or when nothing is undecided.
  Ties broken by fewest tiles (easier question first).
- Answer tiles come from the rules themselves: income tiles are the intervals between the
  distinct thresholds present in the candidate rules (e.g. ≤ ₹1 lakh / ₹1–2.5 lakh /
  > ₹2.5 lakh), so a band never straddles a threshold. "पता नहीं" is always a tile and keeps
  the field unknown → "maybe".
- `VerdictExplainer`: turns the failing (or unknown) rule node into a plain-language reason
  per language from templates in the language pack (e.g. op `lte` on `family_income` →
  "आय सीमा ₹2.5 लाख है"), with the scheme's source_url and fetched_at.
- Income and category are stored as bands in an on-device-only table that the sync layer
  has no mapper for (checked by a unit test), never as exact values.

**Data (Phase 2).** `scheme.steps[]` in data/curated: where (portal URL / school / CSC),
documents (keys into the S18 checklist), deadline, fee, what happens after, helpline; each
step with source_url. ≥ 10 schemes with full steps across central + MP, TN, AP/TS, KA, KL.
CSC locations only if a public directory permits reuse; otherwise "ask your teacher".

**Assisted apply rules.** Draft holds only non-sensitive fields (name, class, school,
district, course). Never stores or enters Aadhaar, OTPs, bank details or portal passwords.
No automation, scripting or scraping of government portals; the app only opens the official
URL in the browser.

**Tracking (Phase 2 local, Phase 4 sync, Phase 8 dashboard).** ApplicationStatus per scheme:
not started → draft ready → applied (student marks) → verification stages from steps[] →
result; renewal date. Reminders via WorkManager on the phone. Status *counts* (not income or
category) sync to the teacher dashboard and Key Metrics.

**Roadmap only:** API Setu and consent-based DigiLocker document fetch after official
partner approval — see docs/ROADMAP.md.

**Checkpoint added to Phase 2/3:** by voice in Hindi, offline, ≤ 6 questions → verdict with
reasons and sources → step-by-step guide.

## 5. Risks

- **R2 APK < 25 MB per ABI.** SQLCipher (~4 MB), Vosk/Kaldi (~8–9 MB), JNA, fonts (~1.5 MB).
  Per-ABI splits, R8 full mode; measured in CI from Phase 1.
- **R3 Cold start < 3 s.** Raw SQLCipher key, DB opened lazily off the main thread,
  Baseline Profiles in Phase 9.
- **R4 FTS5** availability in sqlcipher-android (verify in Phase 2).
- **R6 Keycloak OTP** needs a custom SPI.
- **R7 MinIO** community images discontinued → local-disk S3 interface for dev.
- **R8 Nearby Connections** needs Play services and location/nearby permissions.
- **R9 Pay-after-you-earn** legal review (LEGAL_NOTES.md).
- **R10 External keys with lead time:** Groq (Phase 5), Bhashini/ULCA (Phase 3 for ta/kn/ml
  STT, Phase 6 for transcripts), payment sandbox (Phase 6). Bhashini moves earlier because of
  Add-on A.
- **R11 Real data effort.** ~50 schemes across 6 jurisdictions, each with hand-checked rules.
- **R13 Native-speaker review** for ta, te, kn, ml UI strings and lesson transcripts is a
  people dependency; the team needs a reviewer per language before the demo.
- **R14 Lesson availability.** Openly licensed Electricity content may not exist in every
  language on DIKSHA; then Bhashini translation + review is the path, which needs R10 + R13.
- **R16 Scheme steps data.** Full official steps for ≥ 10 schemes across 6 jurisdictions is
  manual curation work; portals change, so every step carries source_url + fetched_at.
- **R15 Robolectric shaping.** Screenshot tests rely on Robolectric native graphics shaping
  Indic scripts with bundled fonts; checked in Phase 1.

## 6. Conflicts resolved

| Conflict | Resolution |
|---|---|
| Llama/Gemma (diagram, ui-reference) vs gpt-oss-20b | gpt-oss-20b; owner updates the docs |
| TFLite intent model vs rule-based | rule-based |
| S7 under सीखो tab vs avatar | avatar; 3 tabs |
| Thinking ring gold vs #F5A524 | #F5A524 |
| Illustrative reference values | Phase 1 design reference only; three removed entirely |
| S1 six tiles (mr, bn, te, ta, or) vs five languages | the five demo languages first |
