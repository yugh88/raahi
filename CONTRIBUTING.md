# Contributing to Raahi

Thanks for helping. Please read [`docs/PLAN.md`](docs/PLAN.md) first: it lists the rules every
change must follow.

## Ground rules

1. **No synthetic data.** Never add invented people, schemes, scores, reviews or numbers —
   not in code, tests, seeds or screenshots. Use real public records with `source_url` and
   `fetched_at`, or show "not available". Phase 1 screens use values from
   `docs/ui-reference.html`; those resources are named `designref_*` and are removed in Phase 2.
2. **Offline first.** UI code never calls the network. Screen → ViewModel → Repository → Room.
3. **Every label in five languages.** Add strings to `apps/android/i18n/<module>.tsv`
   (columns `key en hi ta te kn ml`), then run `python3 apps/android/i18n/generate.py`.
   Never edit the generated `values*/strings.xml`. New translations are listed as unreviewed
   in `docs/TRANSLATION_REVIEW.md` until a native speaker signs off.
4. **Layouts must survive long words.** Tamil and Malayalam run ~40% longer than Hindi. Key
   labels wrap, never ellipsize; avoid half-width buttons with long labels.
5. **Heavy libraries need an issue first** (APK budget: 25 MB per ABI).

## Workflow

- Small commits with clear messages. No AI attribution lines in commits, PRs or comments.
- `make test` must pass. UI changes: `make screenshots`, then review every changed PNG under
  `src/test/screenshots/` in the diff before committing.
- Icons: edit `apps/android/core/designsystem/icons-src/extra-icons.svg` and run
  `tools/svg2vector.py`; do not hand-edit generated vector XML.

## Code style

Kotlin official style. Match the surrounding code; keep comments for the *why*.
