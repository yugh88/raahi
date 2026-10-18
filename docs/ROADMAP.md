# Roadmap — planned, not built

Items here are deliberately **not** implemented. Each needs an official approval or partner
agreement first.

## API Setu integration

- **What:** use government APIs published on API Setu (apisetu.gov.in) for scheme status
  and verified data, instead of the student retyping details.
- **Precondition:** onboarding as an approved API consumer for each API, with the data
  owner's approval and terms.
- **Until then:** Raahi only prepares non-sensitive drafts with copy buttons and opens the
  official portal; nothing is submitted, scripted or scraped.

## Consent-based DigiLocker document fetch

- **What:** with the student's (or guardian's) explicit consent, fetch issued documents
  (marksheets, income or caste certificates) from DigiLocker to fill the S18 "documents
  ready" checklist automatically.
- **Precondition:** approval as a DigiLocker requester/partner organisation; DPDP-compliant
  consent flow; legal review (see LEGAL_NOTES.md).
- **Data rule:** Raahi would still store only the document's status and dates, never its
  number or contents beyond what the student chooses to keep on the phone.

## After the hackathon: the rest of the build plan

These were cut to make the 19 October 2026 submission. The plan for each is in `PLAN.md` and
`RAAHI_BUILD_PROMPT.md` (Phases 4–9). In the app, their screens show labelled design-reference data.

| Phase | What | Status |
|---|---|---|
| 2 (rest) | Signed pack downloads (Ed25519 via Tink); more schemes (target ~50) including Andhra Pradesh and Telangana once their official pages can be read; NAPS-2 / PMKVY once their current status is confirmed; ITI Electrician career mountain | Bundled pack with SHA-256 checks only; 10 schemes |
| 3 (rest) | Telugu model trial (vosk small-te); Bhashini for ta/kn/ml voice online; reading-level estimate; voice on every screen (onboarding, guide "हो गया") | Hindi voice on the lesson, trap test, scheme check and Ask Didi screens |
| 4 | Backend (Spring Boot 4.1), phone-OTP login, outbox sync, teacher class sync | Not started |
| 5 | Grounded AI answers (gpt-oss-20b via provider interface) with sources | Offline answer bank only |
| 6 | Courses, exam plans, pay-after-you-earn (18+ only; sandbox payments) | Design reference |
| 7 | NGO events, reminders, application-status reminders | "Applied" status is tracked on the phone |
| 8 | Teacher dashboard, Skill Passport, scale proof | Design reference |
| 9 | Baseline Profile (cold start), real 2 GB phone measurements, release signing | Not started |

Content still needed: a teacher review of the Electricity questions; a licensed lesson video
with its transcript; lesson text in ta/te/kn/ml; a native-speaker review of the UI strings
(`TRANSLATION_REVIEW.md`).
