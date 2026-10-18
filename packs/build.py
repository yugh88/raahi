#!/usr/bin/env python3
"""Builds the content packs bundled in the app from data/curated.

A pack is a folder with manifest.json (id, version, built_from, sha256 of every file) and the
JSON data files. The app checks every hash before using a pack. Signing with Ed25519 is added
with pack downloads (Phase 4); bundled packs ship inside the signed APK.

    python3 packs/build.py           # write packs/dist
    python3 packs/build.py --check   # fail if packs/dist is out of date (CI)
"""
import hashlib, json, pathlib, shutil, sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
CURATED = ROOT / "data" / "curated"
DIST = ROOT / "packs" / "dist" / "packs"

PACKS = {
    "core-hi-v1": {
        "title": "Electricity lesson and scheme finder",
        "files": ["lesson-electricity.json", "schemes.json", "scheme-facts.json"],
    },
}


def build(out: pathlib.Path) -> None:
    if out.exists():
        shutil.rmtree(out)
    for pack_id, spec in PACKS.items():
        d = out / pack_id
        d.mkdir(parents=True)
        files = []
        for name in spec["files"]:
            data = (CURATED / name).read_bytes()
            json.loads(data)  # must be valid JSON
            (d / name).write_bytes(data)
            files.append({"path": name, "sha256": hashlib.sha256(data).hexdigest(), "bytes": len(data)})
        manifest = {"id": pack_id, "version": 1, "title": spec["title"], "built_from": "data/curated", "files": files}
        (d / "manifest.json").write_text(json.dumps(manifest, indent=2, ensure_ascii=False) + "\n")


if __name__ == "__main__":
    if "--check" in sys.argv:
        tmp = ROOT / "packs" / ".check"
        build(tmp)
        same = all((DIST / p.relative_to(tmp)).read_bytes() == p.read_bytes() for p in tmp.rglob("*") if p.is_file())
        shutil.rmtree(tmp)
        sys.exit(0 if same else "packs/dist is out of date: run python3 packs/build.py")
    build(DIST)
    print(f"packs written to {DIST.relative_to(ROOT)}")
