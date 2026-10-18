# Bundled fonts

All fonts come from Google Fonts (github.com/google/fonts, `ofl/` directory) under the SIL
Open Font License 1.1 (one licence file per family in this folder).

The files in `src/main/res/font/` are **subsets** made with fontTools: each keeps its own
script plus Basic Latin, ₹, NBSP, general punctuation and the Indic joiners (ZWJ/ZWNJ,
dotted circle), with every OpenType layout feature retained so conjuncts shape correctly.
Noto Sans families are instanced at width 100; the weight axis is kept (variable).

| File | Family | Script range kept |
|---|---|---|
| baloo2.ttf | Baloo 2 | Devanagari U+0900–097F, Vedic ext. |
| mukta_regular/medium/semibold.ttf | Mukta | Devanagari |
| baloo_thambi2.ttf / noto_sans_tamil.ttf | Baloo Thambi 2 / Noto Sans Tamil | Tamil U+0B80–0BFF |
| baloo_tammudu2.ttf / noto_sans_telugu.ttf | Baloo Tammudu 2 / Noto Sans Telugu | Telugu U+0C00–0C7F |
| baloo_tamma2.ttf / noto_sans_kannada.ttf | Baloo Tamma 2 / Noto Sans Kannada | Kannada U+0C80–0CFF |
| baloo_chettan2.ttf / noto_sans_malayalam.ttf | Baloo Chettan 2 / Noto Sans Malayalam | Malayalam U+0D00–0D7F |

Regenerate with `tools/subset-fonts.sh`.
