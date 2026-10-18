# data

- `ingest/` — one script per public source (myScheme, NSP, NAPS, NCS, Skill India Digital,
  SWAYAM, DIKSHA/NCERT, NCO-2015, O*NET, PLFS, AISHE, UDISE+) producing normalized JSON/CSV.
- `curated/` — hand-checked records committed to the repo. Every record has `source_url` and
  `fetched_at`; unknown fields are `null` and the app shows "not available".
- `schema/` — JSON Schemas every curated file must validate against.

No synthetic records, ever: not for seeding, tests, demos or dashboards.
