# Data sources

Every record the app shows comes from a file in `data/curated/`, and every one of those files
names its source. Nothing is invented. Where a fact could not be read from an official source,
it is left out, and the app says "not available" or "confirm this".

All sources were checked on **2026-10-18**.

## Schemes (`data/curated/schemes.json`)

Eleven schemes, with every rule tied to a section of the official document.

| Scheme | Level | Official source | What we used |
|---|---|---|---|
| National Means-cum-Merit Scholarship (NMMSS) | Central | [Revised guidelines, Ministry of Education, 11 Mar 2022](https://scholarships.gov.in/public/schemeGuidelines/NMMSSGuidelines.pdf) | Income ≤ ₹3.5 lakh (§1.1); govt / aided / local body schools only (§1.2); ≥ 55% in class 7, 50% for SC/ST (§2.2); ₹12,000 a year (§1.3); application window from the [NSP listing](https://scholarships.gov.in/All-Scholarships) |
| Post-Matric Scholarship for SC students | Central, via states | [Scheme guidelines, revised March 2021](https://socialjustice.gov.in/writereaddata/UploadFile/PMS_for_SCs_Scheme_Guidelines.pdf) | SC only (§5.2); income ≤ ₹2.5 lakh (§5.3); no management or NRI seats (§5.9) |
| PM-USP Central Sector Scholarship (CSSS) | Central | [Guidelines, from 2022-23](https://scholarships.gov.in/public/schemeGuidelines/CSSS_GUIDLINES_07022024_updated.pdf) | Regular degree (§4(ii)); income ≤ ₹4.5 lakh (§4(v)); 80th percentile is shown as "confirm" (§4(i)); the 2026-27 NSP listing shows renewals only, also shown as "confirm" |
| AICTE Pragati, girls (Degree) | Central | [AICTE guidelines (Degree)](https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_2010_G.pdf) | Girls; first year or lateral second year; income ≤ ₹8 lakh; ₹50,000 a year |
| AICTE Pragati, girls (Diploma) | Central | [AICTE guidelines (Diploma), July 2020](https://scholarships.gov.in/public/schemeGuidelines/AICTE/AICTE_2011_G.pdf) | The same rules for diploma |
| Mukhyamantri Medhavi Vidyarthi Yojana | Madhya Pradesh | [Consolidated order, 09/07/2026](https://medhavikalyan.mp.gov.in/MedhaviChhatra/Resources/Files/Consolidated_Order_Latest_2026.pdf) and [FAQ](https://medhavikalyan.mp.gov.in/MedhaviChhatra/Resources/Files/FAQMMVY(1).pdf) | MP resident (§2.1); income **below ₹8 lakh** (§2.2 — the 2026 order, not the ₹6 lakh many websites still show); 70% MP Board / 85% CBSE-ICSE (§2.3); application steps and documents (FAQ 22–23); helpline from the portal home page |
| Pudhumai Penn | Tamil Nadu | [Social Welfare Dept. scheme page](https://www.tnsocialwelfare.tn.gov.in/website-345/en/pudhumai-penn-scheme) | Girls who studied classes 6–12 in government schools; ₹1,000 a month; first course only |
| Tamil Pudhalvan | Tamil Nadu | [Social Welfare Dept. scheme page](https://www.tnsocialwelfare.tn.gov.in/website-345/en/specilisationswoman-welfare/tamil-pudhalvan) | Boys, government or aided schools, Tamil medium; ₹1,000 a month |
| Post-Matric Scholarship (Backward Classes) | Karnataka | [BCWD page](https://bcwd.karnataka.gov.in/41/post-matric-scholarship/kn) and [2026-27 notice](https://bcwd.karnataka.gov.in/163/2026-27-post-matric-scholarship/kn) | Category-1 income ≤ ₹2.5 lakh, other groups ≤ ₹1 lakh (§2.4); marks 60% / 70% (§2.11); no management or COMEDK seats (§2.6); last date 31.10.2026; helplines |
| Post-Matric Scholarship (ePASS) | Telangana | [ePASS "Who are eligible?"](https://telanganaepass.cgg.gov.in/WhoareEligible.do), checked 2026-10-19 | SC ≤ ₹2.5 lakh, ST ≤ ₹2 lakh, BC ≤ ₹2 lakh (towns) / ₹1.5 lakh (villages); no sponsored, management or spot seats; no part-time or distance courses; 75% attendance to renew; technical helpline. The village limit is shown as "confirm", because income bands are not split at ₹1.5 lakh |
| Post-Matric Scholarship, SC/ST (e-Grantz) | Kerala | [G.O.(P) No. 2/2023/SCSTD, 5 Jan 2023](https://egrantz.kerala.gov.in/egrantz_uploads/aab67494a9a85ace892a8b5d79f29dd6.pdf) | SC/ST; post-matric; merit or reservation allotment (§D1); Aadhaar-seeded bank account (§A3); 75% attendance to renew (§A5) |

### What we could not verify, and so left out

- **Andhra Pradesh.** jnanabhumi.ap.gov.in renders only in a browser, and no official eligibility text could be read, so no rules are shown for this state. (Telangana's page returned 404 on 2026-10-18 but loads with a browser session; it was added on 2026-10-19.)
- **NAPS-2 (apprenticeship stipend) and PMKVY 4.0.** The official pages render only in a browser. NAPS-2's guidelines (25 Aug 2023) ran to 31 March 2026. Secondary sites report an interim extension to 30 September 2026, but we could not find an official order covering October 2026, so they are not shown as schemes.
- **Penkalvi and UMIS portal links (Tamil Nadu).** The department names these portals, but penkalvi.tn.gov.in refused connections, so the app does not deep-link to them. It says "ask your college" instead.
- **Kerala income limit.** The order says the state sets it from time to time, but no number appears in the order, so the app does not apply an income rule for Kerala.

### Income bands

Income is asked as a band, and stored as a band on the phone only. The band edges are exactly the thresholds above:
₹1 lakh, 2.5 lakh, 3.5 lakh, 4.5 lakh and 8 lakh. "Below ₹8 lakh" (MMVY) and "₹8 lakh or less" (Pragati) differ at exactly ₹8 lakh. For the "₹8 lakh or more" band, the app therefore says Pragati is "maybe" rather than guessing.

## Lesson (`data/curated/lesson-electricity.json`)

- **Topic:** Class 10 Science, chapter "Electricity" ([NCERT textbooks](https://ncert.nic.in/textbook.php?jesc1=0-13); Hindi edition "विद्युत").
- **Authors:** the Raahi team wrote the 12 questions, 6 traps and explanations for this app, following the chapter. No textbook text is copied. **A teacher has not reviewed them yet**, and the app says so on every question.
- **No video:** the pack has no video, so the "watch that part" step (S12) is Didi reading the explanation aloud, and the screen says so.

## Speech

- **Recognition:** [Vosk](https://alphacephei.com/vosk/models) `vosk-model-small-hi-0.22` (Apache 2.0), 42 MB. It is a separate pack and is not in the APK.
- **Speech output:** the phone's own text-to-speech in hi-IN. On the test emulator, Google's offline Hindi voice ("hi-in-x-hic-lstm-embedded") was installed once while online, then used in airplane mode.
