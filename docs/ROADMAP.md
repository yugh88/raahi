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
