# Legal notes — items that need review by a qualified lawyer

These are open questions, not legal advice. Nothing here has been reviewed yet. Raahi's demo
moves no real money: payments run only in a payment-gateway sandbox.

## 1. Pay-after-you-earn (course fee deferred until the student earns)

How Raahi limits it today:

- Offered only to students aged 18 or over. Under-18s see only free or sponsor-paid courses,
  and their guardian is informed.
- A fixed fee with a published cap, set before the student starts. It becomes payable only
  after a verified job or stipend above a stated monthly income threshold.
- No interest, no late fee, no penalty, no credit check, no collection agency, no lending
  partner, no data shared with credit bureaus.
- Plain-language explainer (S16) with voice, and a message for the parent.
- The trigger is a verified placement (partner webhook) or an admin-reviewed document.

Questions for review:

1. **Indian Contract Act, 1872 (s.11):** an agreement with a minor is void. Is an 18+ age gate
   based on self-declared date of birth enough, or is verification needed? Can a guardian be
   a party for 17-year-olds, or should under-18s be excluded completely (current design)?
2. **RBI Guidelines on Digital Lending (2022, as amended) and related directions:** does a
   deferred, interest-free, capped course fee count as "credit" or a "loan"? Does it matter
   that no regulated entity or lending service provider is involved, and that the fee is
   owed to the teacher (or covered by a sponsor), not to Raahi?
3. **Consumer Protection Act, 2019 and E-Commerce Rules, 2020:** required disclosures, refund
   and cancellation terms for paid courses and subscriptions sold through the app.
4. **Income threshold and verification:** what proof of income may Raahi ask for, and how long
   may it keep it?
5. **Teacher payouts and sponsor base fees:** GST, TDS and invoicing obligations; whether
   Raahi is a marketplace or a seller.
6. **What happens if the student never earns above the threshold:** confirm that the
   obligation simply never becomes payable and there is no time-based conversion into a debt.

## 2. Digital Personal Data Protection Act, 2023 and DPDP Rules

1. **Children's data (under 18):** verifiable parental consent method that works on a shared
   phone in a village; what counts as "verifiable".
2. **No tracking or behavioural monitoring of children and no targeted advertising:** confirm
   that streaks, reading-level estimates and trap analytics used only to teach the same child
   fall within permitted processing.
3. **Data minimisation:** income and category stay on the device and are never synced;
   Aadhaar and bank numbers are never stored. Confirm document *status* (have / missing /
   expiry date) is acceptable to sync.
4. **Teacher view of student data:** legal basis for a teacher seeing a class's traps and
   progress; what notice is needed.
5. **Partner sharing:** recommendation of a student to an employer or college needs the
   student's (or guardian's) explicit consent each time.
6. **Erasure ("delete my data" by voice):** timelines, and what may be retained (audit log,
   payment records).
7. **Data localisation and cross-border transfer:** the LLM provider (Groq) and Bhashini
   process question text; confirm where that processing happens and what notice is needed.
   Ask-Didi voice audio for ta/kn/ml goes to Bhashini (government service).
8. **Data Protection Officer / grievance officer** contact requirements in the app.

## 3. Content and licences

1. Openly licensed lessons (DIKSHA/NCERT, state boards): confirm each licence allows
   redistribution in offline packs, translation and segmenting.
2. Teacher-uploaded courses: content licence the teacher grants Raahi; takedown process.
3. Fonts: SIL OFL 1.1 (bundled, OFL text included in the repo).
4. Government open data (GODL-India) attribution wording on scheme cards.

## 4. Other

1. Job and apprenticeship listings: liability for listings that later turn out to be fake;
   wording of the fake-job warning.
2. Skill Passport credentials: what the signature attests to, and wording for employers.
3. NGO events and QR check-in: consent for attendance records.
