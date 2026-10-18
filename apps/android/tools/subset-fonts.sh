#!/usr/bin/env bash
# Rebuild the bundled font subsets. Needs: python3 -m pip install fonttools brotli
# Usage: tools/subset-fonts.sh <dir-with-google-fonts-ofl-checkout> <out-dir>
set -euo pipefail
SRC="$1"; OUT="$2"; mkdir -p "$OUT"
LAT="U+0020-007E,U+00A0,U+00D7,U+2010-2027,U+20B9,U+25CC,U+200B-200D"
sub() { python3 -m fontTools.subset "$1" --unicodes="$LAT,$3" --layout-features='*' \
          --no-hinting --desubroutinize --output-file="$OUT/$2"; }
noto() { python3 -m fontTools.varLib.instancer "$1" wdth=100 -q -o "$2"; }
TMP=$(mktemp -d)
noto "$SRC/notosanstamil/NotoSansTamil[wdth,wght].ttf" "$TMP/ta.ttf"
noto "$SRC/notosanstelugu/NotoSansTelugu[wdth,wght].ttf" "$TMP/te.ttf"
noto "$SRC/notosanskannada/NotoSansKannada[wdth,wght].ttf" "$TMP/kn.ttf"
noto "$SRC/notosansmalayalam/NotoSansMalayalam[wdth,wght].ttf" "$TMP/ml.ttf"
sub "$SRC/baloo2/Baloo2[wght].ttf" baloo2.ttf "U+0900-097F,U+1CD0-1CFF,U+A8E0-A8FF"
for w in Regular Medium SemiBold; do
  sub "$SRC/mukta/Mukta-$w.ttf" "mukta_$(echo $w | tr '[:upper:]' '[:lower:]').ttf" "U+0900-097F"
done
sub "$SRC/baloothambi2/BalooThambi2[wght].ttf" baloo_thambi2.ttf "U+0B80-0BFF"
sub "$TMP/ta.ttf" noto_sans_tamil.ttf "U+0B80-0BFF"
sub "$SRC/balootammudu2/BalooTammudu2[wght].ttf" baloo_tammudu2.ttf "U+0C00-0C7F"
sub "$TMP/te.ttf" noto_sans_telugu.ttf "U+0C00-0C7F"
sub "$SRC/balootamma2/BalooTamma2[wght].ttf" baloo_tamma2.ttf "U+0C80-0CFF"
sub "$TMP/kn.ttf" noto_sans_kannada.ttf "U+0C80-0CFF"
sub "$SRC/baloochettan2/BalooChettan2[wght].ttf" baloo_chettan2.ttf "U+0D00-0D7F"
sub "$TMP/ml.ttf" noto_sans_malayalam.ttf "U+0D00-0D7F"
rm -rf "$TMP"
