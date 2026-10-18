# Translation review

Rule (Add-on A): no unreviewed machine or draft translation ships. The ta, te, kn and ml UI
strings in `apps/android/i18n/*.tsv` were drafted by the engineering team on 2026-10-18 and are
**not reviewed**. Hindi strings follow `docs/ui-reference.html` where the reference has them.

A reviewer edits the TSV directly (one row per key), then fills in their name and date below.

| Module (TSV) | hi | ta | te | kn | ml |
|---|---|---|---|---|---|
| core-designsystem | needs review | needs review | needs review | needs review | needs review |
| feature-onboarding | needs review | needs review | needs review | needs review | needs review |
| feature-home | needs review | needs review | needs review | needs review | needs review |
| feature-learn | needs review | needs review | needs review | needs review | needs review |
| feature-roadmap | needs review | needs review | needs review | needs review | needs review |
| feature-askdidi | needs review | needs review | needs review | needs review | needs review |
| feature-earn | needs review | needs review | needs review | needs review | needs review |
| feature-progress | needs review | needs review | needs review | needs review | needs review |
| feature-events | needs review | needs review | needs review | needs review | needs review |
| feature-courses | needs review | needs review | needs review | needs review | needs review |
| feature-teacher | needs review | needs review | needs review | needs review | needs review |
| app | needs review | needs review | needs review | needs review | needs review |

Content packs need review too. `data/curated/lesson-electricity.json`, `schemes.json` and
`scheme-facts.json` have English and Hindi (drafted by the team) and no ta/te/kn/ml yet; the app
falls back to English for those. The Hindi scheme texts restate official rules, so a reviewer
should check them against the linked source documents.

## Questions for reviewers

1. **Didi's name.** Drafts call her அக்கா (ta), అక్క (te), ಅಕ್ಕ (kn), ചേച്ചി (ml), the local word
   for elder sister, instead of "Didi". Keep the local word, or use "Didi" everywhere as a name?
2. **English words inside Indian-language labels.** Hindi keeps words students already use
   ("scholarship", "Pack", "recommend", "stipend"), as the reference does. Check whether the
   same words are better kept in English for your language.
3. **Register.** Labels use the informal imperative (सीखो, कमाओ). Check that the tone fits
   school students in your state and is respectful for teachers' screens.
4. **Gendered forms.** "I will come" (S17) and "I am a teacher" (S2) use both forms where the
   language marks gender (मैं आऊँगा/आऊँगी). Prefer a neutral phrasing if one exists.

## Reviewer log

| Language | Reviewer | Date | Modules |
|---|---|---|---|
| | | | |
