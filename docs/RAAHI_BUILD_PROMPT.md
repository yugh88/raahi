# Raahi — build prompt for Claude Code (v2)

How to use:
1. Make an empty folder `raahi/`, then put these files in `raahi/docs/`: this file, `ui-reference.html`, `didi.svg`, `didi-wave.svg`, `raahi-architecture.png`, `raahi-architecture.eraser`.
2. Open Claude Code in `raahi/` and paste **Prompt 0**. Wait for its plan, reply "go".
3. Paste **Prompt 1 … Prompt 9** one at a time. Each phase ends with a checkpoint you must see working before moving on.

---

## Prompt 0 — master prompt (paste once)

```
You are the lead engineer for "Raahi" (राही), our entry for the Citi India Hackathon problem
"Reimagining Education & Economic Empowerment for Rural India". Read every file in docs/ first.
docs/ui-reference.html is the approved visual design (section "UI and UX": 8 phone screens,
Didi character, colours, fonts, spacing). Every NEW screen described below must use the same
design system so the whole app looks like one product. docs/raahi-architecture.png is the
approved architecture.

PRODUCT
A voice-first Android app for rural students (Class 9 to graduation) and village teachers,
built for slow 2G and patchy internet. Didi, an AI helper character, guides each student:
DISCOVER a career → CLIMB its roadmap → LEARN through a no-fear "trap" loop → EARN through
scholarships, schemes, apprenticeships, mentors and jobs. Reputed teachers publish courses and
earn, including a "pay after you earn" option for students.

NON-NEGOTIABLE RULES
1. Low-bandwidth-first. Normal case = slow 2G/3G and patchy signal; every student screen must
   also work in airplane mode. UI never calls the network directly: Compose screen → ViewModel →
   Repository → Room (single source of truth). Test every feature at 50 kbps and offline.
2. Voice AI on 2G: speech-to-text and text-to-speech run ON THE PHONE; only text goes to the
   cloud LLM (~2 KB per question, streamed reply). Never upload audio.
3. No generative LLM on low-end phones. On-phone intelligence = Vosk speech, per-screen grammar
   + rule-based intent mapping (no trained intent model), Kotlin rules engine, career scorer,
   SQLite FTS5 answer-bank search, and the trap engine (topic tags, no AI needed).
   Optional Gemma 3 1B (4-bit, LiteRT-LM) only on 6 GB+ phones behind a flag (Phase 9).
4. Public, real data only. Never create synthetic/fake records, users, schemes, students,
   teachers, courses, reviews or metrics — not for seeding, tests, demos or dashboards. Every
   knowledge record stores source_url and fetched_at. Unknown fields stay null and show
   "not available". Test fixtures are copied from real public records and cite the source.
   Course videos come only from openly licensed sources (check each item's licence, e.g.
   DIKSHA/NCERT) or from content our own team records. Ratings/reviews start empty.
5. Low-end Android: minSdk 26, release APK < 25 MB (speech models, videos and packs download
   separately), cold start < 3 s on a 2 GB RAM phone. Ask me before adding heavy libraries.
6. Security (OWASP MASVS + India DPDP Act): SQLCipher DB with key in Android Keystore, TLS +
   certificate pinning, short-lived OAuth2 tokens, Ed25519-signed content packs, never store
   Aadhaar/bank numbers, eligibility-only fields (income, category) stay on device and are never
   synced, parental consent for under-18s, no ads, no tracking SDKs, no loot boxes or paid coins.
7. Open source: Apache-2.0 licence, README, CONTRIBUTING, OpenAPI specs.
8. Git: small commits, clear messages. Do NOT add "Co-Authored-By" lines or any AI attribution
   to commits, PRs or code comments.
9. If anything is unclear, a library fails, or a model/provider has changed, stop and give me
   options. Never silently substitute.

MONOREPO
raahi/
  apps/android/        Kotlin, Jetpack Compose, Hilt, Navigation Compose, Media3 (ExoPlayer)
  apps/dashboard/      Next.js + TypeScript (teachers, NGOs, govt, employers)
  services/core/       Java 21, Spring Boot 4.1 (profiles, content, courses, payments, sync, partners)
  services/ai/         Python 3.11+, FastAPI (RAG, career explain, answer bank, safety, lesson planner)
  data/ingest/         scripts that fetch public datasets → normalized CSV/JSON
  data/curated/        hand-checked records with source_url + fetched_at (committed)
  packs/               pack builder + Ed25519 signing tool
  infra/               docker-compose.yml: PostgreSQL 16 + pgvector, Redis, Keycloak, MinIO,
                       Redpanda; k8s/ later
  docs/

EXACT MODELS AND TOOLS
On phone: Vosk vosk-model-small-hi-0.22 (42 MB), Android TextToSpeech hi-IN, SQLite FTS5,
Room + SQLCipher (net.zetetic sqlcipher-android), WorkManager, Google Nearby Connections,
Media3 ExoPlayer (offline playback with segment seeking), Hilt.
Cloud: openai/gpt-oss-20b (open-weight, Apache-2.0) via Groq free tier behind a provider
interface (swappable to self-hosted gpt-oss-20b on vLLM/Ollama), openai/gpt-oss-safeguard-20b
as safety filter, intfloat/multilingual-e5-small embeddings (384-dim, CPU) in pgvector (HNSW),
Bhashini for ASR/TTS/translation in other languages (key stays on server).
Check the provider's current model list before Phase 5 and tell me if anything changed.

ANDROID MODULES
:app, :core:designsystem, :core:data, :core:sync, :core:security, :core:voice, :core:engine
(rules, career scorer, answer search, trap engine, spaced repetition), :core:media,
:feature:onboarding, :feature:home, :feature:roadmap, :feature:learn, :feature:courses,
:feature:askdidi, :feature:earn, :feature:progress, :feature:events, :feature:teacher.

DESIGN SYSTEM (:core:designsystem — match ui-reference.html exactly; light theme only, never dark)
Colours: bg #F4F6F9, surface #FFFFFF, surface2 #EEF1F6, line #D9DEE7, ink #121826,
muted #586174, neel #2440A8, neelTint #E4E9FB, gold #A86F00, goldTint #FBEFD2,
didiOrange #F5A524, leaf #1F6F43, leafTint #DFF1E6, clay #B0402A, clayTint #FBE3DD.
Fonts: Baloo 2 (UI, Devanagari) + Mukta (English sublabels), bundled fallback.
Shapes: tiles 16dp, cards 14dp, pills fully rounded; touch targets ≥ 56dp.
Components: BigMicButton (66dp, states idle/listening/thinking/speaking/offline), DidiAvatar
(didi.svg / didi-wave.svg as VectorDrawables; ring: listening=neel, thinking=didiOrange #F5A524,
speaking=leaf, offline=dashed), DidiBubble, ChoiceTile (speaks its label on tap),
OpportunityCard, StatusPill, OfflineBanner, BottomTabs (घर · सीखो · कमाओ — exactly 3 tabs;
profile/progress via avatar at top right), MountainPath, TrapChip, CourseCard, PricePill,
VideoSegmentPlayer.
UX rules: max 3 primary choices per screen; tap any text to hear it; pictures before words;
autosave everything; never show an error screen — show "saved, will send when network comes";
photo-avatar profiles for shared phones; typing always available as a fallback to voice.

SCREENS (S1–S8 exist in ui-reference.html; S9–S18 are new, same style)
S1 Language picker — Didi waving, 6 language tiles, mic.
S2 Didi onboarding — 5 spoken questions (name, class/status, language, interests, goal) →
   persona (Class 9–10 / Class 11–12 / graduate-ITI-dropout / teacher).
S3 Home — greeting, streak, offline banner, exactly 3 big cards: today's lesson, money you may
   get, your mountain; Didi nudge + mic; upcoming NGO session card when one exists.
S4 Lesson question — picture, question, 3 spoken options, instant feedback.
S5 Ask Didi — answer bubble with SOURCE line, "फिर सुनो", "कागज़ लिस्ट".
S6 Earn — matched cards (scholarship + deadline pill, apprenticeship, short course, mentor),
   "दीदी के साथ फ़ॉर्म भरो".
S7 Progress — streak, badges, school-vs-school rank, Skill Passport QR.
S8 Teacher class view — learners on shared phone, Didi alert, share pack, recommend student.
S9 Career mountain (roadmap.sh-style) — a vertical mountain path for the chosen career:
   base = foundation skills, middle = lessons/courses/exams as checkable nodes (done = leaf,
   current = neel with climber, locked = muted), summit = outcomes (scholarship, apprenticeship,
   job) with real matched items. Tap a node → what it is, why it matters, time needed, start.
S10 Trap test — short topic-tagged test (5–8 questions), one at a time, no score shown.
S11 Trap summary — "आप 3 जगह फँसे: भिन्न · इकाई बदलना · प्रतिशत" as TrapChips; for each trap a
   button "वीडियो का वो हिस्सा देखो" and "फिर से कोशिश". Wording is always encouraging; never
   "wrong"/"fail"/marks.
S12 Video segment player — plays only the linked segment (start–end seconds) with a live
   transcript below (tap a line to jump), speed 0.75/1/1.25, works offline from the pack.
S13 Trap cleared — celebration, climber moves up the mountain, badge, next spaced re-check
   date ("3 दिन बाद दीदी फिर पूछेगी").
S14 Courses — CourseCards from verified teachers (photo, name, subject, "verified" badge,
   lessons count, download size, PricePill: मुफ़्त · ₹/module · ₹/month all-access ·
   "नौकरी के बाद दो" pay-after-you-earn). Filters: my career, exams, language. Ratings shown only
   when real reviews exist.
S15 Exam prep plan (GregMat-style) — one low monthly all-access plan per exam (e.g. SSC,
   Railways, ITI entrance, NEET/JEE foundation, Class 10 boards): daily study plan generated
   from the student's traps and exam date, practice sets, weekly live doubt class, progress.
S16 Pay-after-you-earn explainer — plain-language card + voice: "अभी मुफ़्त पढ़ो। नौकरी या
   stipend ₹X/महीना से ज़्यादा मिलने पर ही, एक तय छोटी रकम दो। कोई ब्याज नहीं, कोई क़र्ज़ नहीं।"
   Shows sponsor name if a CSR/NGO sponsor covers the base fee; parent message button.
S17 NGO events — list of upcoming sessions (career camp, workshop) with date, place, distance;
   one-tap RSVP; reminder set on the phone; QR check-in at the venue.
S18 Documents ready — checklist per opportunity (have / missing / expiring soon), where to get
   each document, reminder before expiry. Stores only status and dates, never ID numbers.
Teacher screens: T1 class traps heatmap (which topics block most students), T2 assign
segment/test, T3 course studio (upload/record lesson, auto-transcript, tag each question to a
topic + video start/end), T4 pricing (free | per_module | subscription | pay_after_earn),
T5 earnings, T6 mixed-grade lesson planner (AI, online), T7 recommend student for opportunity.

ENGINES (:core:engine, pure Kotlin, unit-tested)
- TrapEngine: every question has topic_id, difficulty, video_id, seg_start, seg_end and a
  variation group. After a test → list of traps (topics answered wrong) → linked segments.
  Retry draws fresh variations from the same group. Cleared = 2 correct in a row.
- SpacedRepetition: cleared traps re-appear at 1, 3, 7, 21 days (Leitner boxes).
- EligibilityEngine: JSON rules (ops eq, neq, in, lte, gte, between, exists; all/any/not);
  unknown profile field → "maybe eligible", never a silent no.
- CareerScorer: interest answers (O*NET RIASEC) → NCO-2015 occupations; always include at
  least one vocational and one non-STEM path; pay band from PLFS.
- RoadmapBuilder: renders the student's slice of the Pathway Graph as the mountain (S9).
- AnswerSearch: SQLite FTS5 over verified answer bank; low confidence → queue for cloud.
- IntentRouter: Vosk per-screen grammar + rule mapping.

PATHWAY GRAPH (services/core, Postgres tables)
career → skills → topics → lessons/video segments → courses (SWAYAM, Skill India, teacher
courses) → exams → scholarships/schemes → apprenticeships/jobs → mentors. Built only from
data/curated with source_url. Each student's slice ships inside their packs.

COURSES AND TEACHER EARNINGS (services/core)
- Teacher verification: ID + qualification check by admin before "verified" badge.
- Course = pack of lessons (video segments ≤ 6 min, 240p/360p low bitrate or slides + audio),
  transcripts, tagged question bank. Packs download on Wi-Fi, from another phone, or from an
  SD card.
- Pricing types: free | per_module | subscription | pay_after_earn.
- pay_after_earn: sponsor (CSR/NGO/employer) can pay a base fee to the teacher; the student
  owes a fixed, capped amount only after a verified job/stipend above an income threshold;
  no interest, no penalties, written in plain language, parent informed; trigger via
  verified placement (partner API) or document upload reviewed by admin.
- Live classes: optional, audio-first, recorded with transcript and added to the pack.
- Payments online only, via a payment gateway sandbox in the demo; no money moves in tests.

SYNC, VOICE, BACKEND, DATA, SECURITY: as in the architecture — outbox events, delta packs,
WorkManager (periodic, app open, charging), daily sync < 300 KB, Nearby Connections for packs
only; Spring Boot REST /api/v1 with OpenAPI, Keycloak OAuth2 + phone OTP (dev stub logs OTP);
FastAPI /ask (RAG + citations + refusal when unsupported + Redis cache), /career/explain,
/lesson-plan, /answer-bank/export (human_reviewed only), Bhashini proxy; Next.js dashboard.
Data sources (public): myScheme, NSP, NAPS/apprenticeshipindia, NCS, Skill India Digital,
SWAYAM, DIKSHA/NCERT, NCO-2015, O*NET Interest Profiler, PLFS, AISHE, UDISE+, Bhashini.

DEMO SLICE (build this first, end to end)
Language Hindi. Tracks: Class 10 Science (Electricity chapter) and ITI Electrician career
mountain. ~50 curated MP + central schemes/scholarships. One NGO session posted by our team as
a real test event, clearly labelled.

DEFINITION OF DONE (every phase)
Builds with one command; unit tests pass; every student screen tested at 50 kbps and in
airplane mode; no secrets in repo; docs/DEMO.md updated with how to demo the phase.

Reply first with: repo tree, exact library versions, risks, and anything in this prompt you
think is wrong. Wait for my "go".
```

---

## Decisions after Prompt 0 (2026-10-18)

```
- Spring Boot 4.1.1 replaces Spring Boot 3 (3.5 open-source support ended 2026-06-30).
- LLM: gpt-oss-20b via the provider interface (not Llama/Gemma). Intent: rule-based only
  (Vosk per-screen grammar + rules), no TFLite intent model.
- S7 Progress opens from the avatar at top right; bottom tabs stay exactly 3.
- Didi "thinking" ring = didiOrange #F5A524; gold #A86F00 is for text and pills.
- Reference-screen example values are used only in Phase 1 previews/screenshot tests, labelled
  "design reference". From Phase 2 every value comes from a real record or shows
  "not available". "Sunita", "Rampur #2 of 12" and "₹1,240 earned" are removed entirely.
  Mentor cards only for real onboarded mentors (with consent).
- APK: App Bundle / per-ABI splits for arm64-v8a AND armeabi-v7a, each < 25 MB, measured in
  CI from Phase 1. Speech models are downloaded packs.
- SQLCipher: random raw key wrapped by Android Keystore, no key derivation.
- FTS5: verify it exists in sqlcipher-android; else FTS4 and tell the owner.
- Ed25519 verify on Android: Google Tink (approved).
- Keycloak phone-OTP SPI approved; the dev stub logs the OTP and is never in prod config.
- Object storage (dev): packs served from local disk by the core service behind an
  S3-compatible interface; provider options presented in Phase 4.
- Pay-after-you-earn: 18+ only; under-18s get free or sponsor-paid courses only (guardian
  informed). Fixed, capped fee; no interest, credit, lending or penalties. Sandbox payments
  only. Open legal questions in docs/LEGAL_NOTES.md.
- Toolchain: JDK 21 through Gradle toolchains; Node 22 LTS.
```

## Add-on A — five languages

```
The demo must support FIVE languages: Hindi (hi), Tamil (ta), Telugu (te), Kannada (kn),
Malayalam (ml), plus English (en) sublabels. Update the plan and any code already written.

1. LANGUAGE PACKS. Each language = one downloadable pack: app strings, Didi's recorded
   prompts, speech model if one exists, lesson transcripts, a technical-term glossary
   (e.g. प्रतिरोध / மின்தடை / నిరోధం / ರೋಧ / പ്രതിരോധം = resistance) and the state scheme set.
   Adding a language must need no code change. Android strings in values-hi, values-ta,
   values-te, values-kn, values-ml; S1 language picker shows these five tiles first.
2. FONTS per script (Google Fonts, bundled fallback): Devanagari = Baloo 2 + Mukta,
   Tamil = Baloo Thambi 2 + Noto Sans Tamil, Telugu = Baloo Tammudu 2 + Noto Sans Telugu,
   Kannada = Baloo Tamma 2 + Noto Sans Kannada, Malayalam = Baloo Chettan 2 +
   Noto Sans Malayalam. Same sizes and weights as the design system.
3. LAYOUT. Tamil and Malayalam words run long: every component must handle text 40% longer
   than Hindi without clipping (wrap, never ellipsize key labels). Add screenshot tests for
   all screens in all five languages.
4. VOICE.
   - Hindi: Vosk vosk-model-small-hi-0.22, works offline.
   - Telugu: try vosk-model-small-te-0.42 offline but measure accuracy; if poor, use online.
   - Tamil, Kannada, Malayalam: speech-to-text via Bhashini through services/ai when online
     (send compressed short audio only for these, max 5 s, Opus 16 kbps, and say so in the
     data budget); with no signal, the screen falls back to picture + tap and Didi still
     speaks.
   - Text-to-speech: Android TextToSpeech hi-IN, ta-IN, te-IN, kn-IN, ml-IN; if the phone
     lacks a voice, prompt a one-time voice-data download on Wi-Fi and use pre-recorded
     prompts meanwhile.
5. CONTENT. Class 10 Science (Electricity) in all five languages: use openly licensed
   state-board/DIKSHA lessons in that language where they exist; otherwise translate the
   transcript with Bhashini and get it reviewed by a native speaker (record reviewer name and
   date in the pack manifest). Never ship unreviewed machine translations.
6. SCHEMES. ~50 real schemes: central schemes + Madhya Pradesh (hi), Tamil Nadu (ta),
   Andhra Pradesh/Telangana (te), Karnataka (kn), Kerala (ml), each with source_url and
   fetched_at from official pages.
7. AI ANSWERS. /ask replies in the student's language with key English terms in brackets.
   Add an evaluation set of 10 real questions per language written by native speakers on our
   team; report grounded-answer rate per language. If gpt-oss-20b is weak in a language,
   generate in English from the same sources and translate with Bhashini, then flag for
   review. Never show an answer without its source.
8. READING LEVEL. Store a reading-level estimate per student (from onboarding and how often
   they tap to hear). Low level = more audio and pictures, less text; same screens.
9. DEMO SCRIPT. docs/DEMO.md: run the same flow (onboard → mountain → trap loop → scholarship
   match → Ask Didi) once in Hindi offline and once in Tamil on throttled 2G.
```

---

## Prompt 1 — design system and all screens (static)

```
Phase 1. Monorepo skeleton + Android app shell + :core:designsystem + ALL screens S1–S18 and
teacher screens T1–T7 as static Compose UI (hard-coded UI state is fine, but use real names
from docs/ui-reference.html or real public records, never invented people or numbers).
S1–S8 must match ui-reference.html pixel-close. S9–S18 and T1–T7 follow the descriptions in
the master prompt with the same tokens and components.
Compose previews + Roborazzi screenshot tests for every screen. Hindi strings in values-hi.
Checkpoint: I click through every screen on a 2 GB phone/emulator; light theme only;
navigation uses exactly 3 bottom tabs.
```

## Prompt 2 — local data, packs, engines (fully offline)

```
Phase 2. Room + SQLCipher entities: Profile, Career, Skill, Topic, Lesson, VideoSegment,
Transcript, Question (topic, variation group, segment link), Attempt, Trap, ReviewSchedule,
Course, Teacher, Scheme, EligibilityRule, Opportunity, Mentor, Event, DocumentStatus,
AnswerBankEntry (FTS5), OutboxEvent, ContentPack.
Pack format: zip with manifest.json, JSON data, media, Ed25519 signature; packs/ build tool.
First packs: Class 10 Science Electricity (openly licensed lessons, real transcript, tagged
questions), ITI Electrician mountain, 50 curated schemes, answer bank (human_reviewed only).
Implement TrapEngine, SpacedRepetition, EligibilityEngine, CareerScorer, RoadmapBuilder,
AnswerSearch with unit tests on real fixtures.
Wire S2–S18 to repositories.
Checkpoint (airplane mode from install): onboard → mountain → trap test → trap summary →
exact video segment with transcript → retry → trap cleared → climber moves up → scholarship
match with source.
```

## Prompt 3 — voice

```
Phase 3. Vosk (vosk-model-small-hi-0.22, downloaded as a pack), per-screen grammar, rule-based
IntentRouter, BigMicButton states drive Didi's ring, Android TTS hi-IN for every text,
tap-to-hear everywhere, typing fallback, low-confidence → highlight picture choices.
Checkpoint: complete onboarding, a trap test and a retry using only voice, offline.
```

## Prompt 4 — backend, auth, sync

```
Phase 4. infra/docker-compose.yml; services/core with Flyway, OpenAPI, Keycloak OAuth2 + OTP
dev stub, rate limits, audit log; endpoints for profiles, sync push/pull, packs, courses,
teachers, events, opportunities, mentors, partners (API key + webhooks), teacher classes,
recommendations. :core:sync outbox + delta pull + signed pack download + cert pinning.
Conflict test: same profile on two phones merges progress without loss.
Checkpoint: trap tests offline on two phones → network on → both sync; teacher sees the class
traps heatmap.
```

## Prompt 5 — grounded AI service

```
Phase 5. services/ai: ingest data/curated into pgvector with multilingual-e5-small; /ask with
retrieval → gpt-oss-20b (provider interface) → simple Hindi answer with source_url citations →
gpt-oss-safeguard-20b → Redis cache; refuse when no source supports the answer.
/career/explain, /lesson-plan (mixed-grade planner for teachers), /answer-bank/export.
Evaluation: 50 real student-style questions written by our team from public FAQ pages; report
grounded-answer rate and refusal rate. No generated test questions.
Checkpoint at 50 kbps: a question not in the answer bank gets a cited spoken answer in seconds
and is saved for offline replay.
```

## Prompt 6 — courses, exam plans, pay-after-you-earn

```
Phase 6. Teacher onboarding + verification flow; course studio (T3): upload/record, automatic
transcript (Bhashini ASR online), tag questions to topic + segment; pricing (T4) with all four
types; S14 Courses, S15 GregMat-style exam plan (daily plan built from the student's traps and
exam date), S16 pay-after-you-earn explainer and agreement screen with parent message;
earnings (T5). Payment gateway in sandbox only. Placement verification via partner webhook or
admin-reviewed document.
Checkpoint: a teacher publishes a course pack, a student downloads it over Wi-Fi/another phone,
learns offline, and the pay-after-earn state machine moves correctly in tests.
```

## Prompt 7 — earn helpers, events, engagement

```
Phase 7. S18 documents-ready checklist with expiry reminders; parent voice message (TTS
script from the opportunity data); money-in-hand view (stipend − travel); fake-job warning
(checks against verified listings + red-flag rules such as fees); S17 NGO events from the
dashboard with push (when online), SMS and on-phone reminders, RSVP, QR check-in; weekly
leagues, class/school boards with nicknames, Bluetooth quiz duels via Nearby Connections;
optional daily brain games that unlock after the day's lesson (engagement only).
Checkpoint: NGO posts a session in the dashboard → student phone reminds the evening before
with no signal → QR check-in adds a Skill Passport entry.
```

## Prompt 8 — dashboard, Skill Passport, scale proof

```
Phase 8. Next.js dashboard: class traps heatmap, assignments, recommendations, courses and
earnings, NGO events and attendance, partner opportunity posting, public statistics panel
(UDISE+, ASER) with source labels. Skill Passport as W3C Verifiable Credentials signed by the
server, QR on S7, offline verifier screen. Load test (k6 or Locust) simulating 10,000 users'
daily traffic; record real p95 latency in docs/SCALE.md. Usability test kit in
docs/USABILITY_TEST.md (5 tasks, success criteria, empty results sheet — never invent results).
Checkpoint: passport verified offline by QR; load-test report committed.
```

## Prompt 9 — hardening and demo

```
Phase 9. MobSF + OWASP ZAP fixes, dependency scanning in GitHub Actions, Play Integrity,
remote logout, delete-my-data by voice, parental consent; APK < 25 MB, cold start < 3 s,
Baseline Profiles; optional Gemma 3 1B (LiteRT-LM) behind a flag for 6 GB+ phones.
docs/DEMO.md 5-minute script: voice onboarding → career mountain → trap test → exact video
segment → trap cleared → scholarship match with source and documents check → question to
Didi on throttled 2G → teacher heatmap → course with pay-after-you-earn → NGO event reminder.
Checkpoint: the full demo runs twice in a row without a crash.
```

---

## Quick fixes to paste any time
- "Compare screen N with docs/ui-reference.html and the master prompt; list every difference and fix it."
- "Find every network call reachable from a student screen; route each through repository/sync."
- "Search for any invented records, names, numbers or reviews; replace with real sourced data or remove."
