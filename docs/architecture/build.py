#!/usr/bin/env python3
"""Generates the Raahi architecture diagram in two forms from one layout:

  architecture.excalidraw  open and edit at https://excalidraw.com (File → Open)
  architecture.svg         hand-drawn rendering for the README (Virgil font embedded)

    python3 docs/architecture/build.py

Edit the layout below, not the outputs.
"""
import base64
import json
import math
import random
from pathlib import Path

HERE = Path(__file__).resolve().parent
FONT = HERE / "Virgil.woff2"  # Excalidraw's hand-drawn font, SIL OFL 1.1

# Excalidraw palette: (stroke, background)
BLUE, GREEN, YELLOW = ("#1971c2", "#a5d8ff"), ("#2f9e44", "#b2f2bb"), ("#f08c00", "#ffec99")
VIOLET, RED, GREY = ("#6741d9", "#d0bfff"), ("#e03131", "#ffc9c9"), ("#495057", "#e9ecef")
WHITE = ("#1e1e1e", "transparent")

W, H = 1600, 1100

# id: (x, y, w, h, label, colour, dashed, font size)
BOXES = {
    "title": (40, 18, 1520, 40, "Raahi — how it fits together (solid = running today, dashed = planned)", WHITE, False, 26),
    "student": (40, 170, 220, 120, "Student\nshared low-end phone\n2G or no signal", BLUE, False, 18),
    "teacher": (40, 350, 220, 100, "Teacher\nclass phone\n(screens: design ref.)", GREY, True, 18),
    "phone": (300, 80, 860, 790, "", WHITE, False, 18),
    "phone_label": (320, 92, 820, 30, "Android phone — built for slow 2G: works when the signal drops (no network needed today)", WHITE, False, 20),
    "ui": (330, 140, 380, 120, "Jetpack Compose UI\nS1–S21 student · T1–T7 teacher\n5 languages · tap-to-hear", BLUE, False, 18),
    "voice": (740, 140, 390, 120, "Didi's voice (on the phone)\nVosk ASR: tile grammar / free speech\nAndroid TTS hi-IN", VIOLET, False, 18),
    "vm": (330, 300, 800, 60, "RaahiViewModel — one state holder, stateless screens", GREY, False, 18),
    "engine": (330, 395, 800, 200, "", GREY, False, 18),
    "engine_label": (345, 402, 770, 26, "core:engine — pure Kotlin, tested on the real curated rules", WHITE, False, 18),
    "e1": (345, 440, 145, 135, "Eligibility\nEngine\npass/fail/\nunknown", GREEN, False, 16),
    "e2": (500, 440, 145, 135, "Question\nSelector\n≤ 6 questions\nmost decided", GREEN, False, 16),
    "e3": (655, 440, 145, 135, "Verdict\nExplainer\nreason =\nthe rule's text", GREEN, False, 16),
    "e4": (810, 440, 145, 135, "TrapEngine\n+ Spaced\nRepetition\n1·3·7·21 days", GREEN, False, 16),
    "e5": (965, 440, 150, 135, "Answer\nSearch\nsourced or\n\"I don't know\"", GREEN, False, 16),
    "data": (330, 630, 390, 175, "core:data\nRoom + SQLCipher\nkey: 256-bit, wrapped by\nAndroid Keystore\nprofile · answers · traps\nscheme bands · applications", BLUE, False, 17),
    "pack": (740, 630, 390, 175, "Content pack (bundled)\nmanifest + SHA-256 per file\nElectricity lesson · 11 schemes\neligibility questions", YELLOW, False, 17),
    "privacy": (740, 815, 390, 48, "Income & caste: bands, on this phone only\nnever Aadhaar, OTP, bank or passwords", RED, False, 15),
    "sources": (1220, 290, 340, 140, "Official sources\nNSP · AICTE · MoE · MSJE\nMP · TN · KA · TG · KL\nNCERT Class 10 Science", GREY, False, 18),
    "curated": (1220, 470, 340, 115, "data/curated/*.json\nhand-checked, every rule\ncites its § · checked 18–19 Oct", YELLOW, False, 18),
    "build": (1220, 640, 340, 75, "packs/build.py\n(CI: --check)", YELLOW, False, 18),
    "model": (1220, 150, 340, 90, "Vosk Hindi model pack\n42 MB, installed once", VIOLET, False, 18),
    "planned_label": (740, 905, 420, 30, "PLANNED — not built yet", WHITE, False, 20),
    "core": (300, 945, 200, 120, "Core service\nSpring Boot 4.1\nOTP login · sync", GREY, True, 17),
    "ai": (520, 945, 200, 120, "AI service\ngpt-oss-20b\ngrounded, sourced", GREY, True, 17),
    "dash": (740, 945, 200, 120, "Teacher\ndashboard\nclass traps", GREY, True, 17),
    "bhashini": (960, 945, 200, 120, "Bhashini\nta / kn / ml voice\n(online)", GREY, True, 17),
}

# (from, to, label, dashed)
ARROWS = [
    ("student", "ui", "", False),
    ("teacher", "ui", "", True),
    ("ui", "vm", "", False),
    ("voice", "vm", "text", False),
    ("vm", "engine", "", False),
    ("engine", "data", "", False),
    ("engine", "pack", "reads", False),
    ("sources", "curated", "read & checked", False),
    ("curated", "build", "", False),
    ("build", "pack", "bundled in APK", False),
    ("model", "voice", "", False),
    ("data", "core", "sync outbox", True),
]

rng = random.Random(7)


def centre(b):
    x, y, w, h = BOXES[b][:4]
    return x + w / 2, y + h / 2


def edge_point(b, tx, ty):
    """Where the line from the box centre towards (tx, ty) leaves the box."""
    x, y, w, h = BOXES[b][:4]
    cx, cy = x + w / 2, y + h / 2
    dx, dy = tx - cx, ty - cy
    if dx == 0 and dy == 0:
        return cx, cy
    sx = (w / 2) / abs(dx) if dx else math.inf
    sy = (h / 2) / abs(dy) if dy else math.inf
    s = min(sx, sy)
    return cx + dx * s, cy + dy * s


def arrow_ends(a, b):
    ax, ay = centre(a)
    bx, by = centre(b)
    x1, y1 = edge_point(a, bx, by)
    x2, y2 = edge_point(b, ax, ay)
    return x1, y1, x2, y2


# ---------------- Excalidraw ----------------

def base(id_, type_, x, y, w, h, stroke, bg, dashed):
    return {
        "id": id_, "type": type_, "x": x, "y": y, "width": w, "height": h, "angle": 0,
        "strokeColor": stroke, "backgroundColor": bg, "fillStyle": "solid", "strokeWidth": 2,
        "strokeStyle": "dashed" if dashed else "solid", "roughness": 1, "opacity": 100,
        "groupIds": [], "frameId": None, "roundness": {"type": 3}, "seed": rng.randint(1, 2**31),
        "version": 1, "versionNonce": rng.randint(1, 2**31), "isDeleted": False,
        "boundElements": [], "updated": 1, "link": None, "locked": False,
    }


def text_el(id_, x, y, w, h, text, size, container=None, align="center"):
    lines = text.count("\n") + 1
    el = base(id_, "text", x, y, w, size * 1.25 * lines, "#1e1e1e", "transparent", False)
    el.update({
        "roundness": None, "text": text, "originalText": text, "fontSize": size, "fontFamily": 1,
        "textAlign": align, "verticalAlign": "middle", "containerId": container,
        "lineHeight": 1.25, "baseline": int(size * 0.9), "autoResize": True,
    })
    if container is None:
        el["y"] = y + (h - el["height"]) / 2
    return el


def excalidraw():
    els = []
    for id_, (x, y, w, h, label, (stroke, bg), dashed, size) in BOXES.items():
        bare = bg == "transparent"
        if bare and label:  # free text (titles)
            els.append(text_el(id_, x, y, w, h, label, size, align="left" if id_.endswith("label") or id_ == "title" else "center"))
            continue
        box = base(id_, "rectangle", x, y, w, h, stroke, bg, dashed)
        if id_ == "phone":
            box["strokeWidth"] = 3
        els.append(box)
        if label:
            t = text_el(id_ + "_t", x, y, w, h, label, size, container=id_)
            box["boundElements"].append({"type": "text", "id": t["id"]})
            els.append(t)
    for i, (a, b, label, dashed) in enumerate(ARROWS):
        x1, y1, x2, y2 = arrow_ends(a, b)
        ar = base(f"arrow{i}", "arrow", x1, y1, x2 - x1, y2 - y1, "#1e1e1e", "transparent", dashed)
        ar.update({
            "roundness": {"type": 2}, "points": [[0, 0], [x2 - x1, y2 - y1]], "lastCommittedPoint": None,
            "startBinding": {"elementId": a, "focus": 0, "gap": 4}, "endBinding": {"elementId": b, "focus": 0, "gap": 4},
            "startArrowhead": None, "endArrowhead": "arrow",
        })
        for end in (a, b):
            next(e for e in els if e["id"] == end)["boundElements"].append({"type": "arrow", "id": ar["id"]})
        els.append(ar)
        if label:
            t = text_el(f"arrow{i}_t", 0, 0, 10, 10, label, 15, container=ar["id"])
            ar["boundElements"].append({"type": "text", "id": t["id"]})
            els.append(t)
    return {
        "type": "excalidraw", "version": 2, "source": "https://excalidraw.com", "elements": els,
        "appState": {"gridSize": None, "viewBackgroundColor": "#ffffff"}, "files": {},
    }


# ---------------- hand-drawn SVG ----------------

def jitter(v, amount=1.6):
    return v + rng.uniform(-amount, amount)


def rough_line(x1, y1, x2, y2):
    """Two slightly different strokes with a bowed middle, like Excalidraw's rough.js."""
    paths = []
    length = math.hypot(x2 - x1, y2 - y1)
    bow = min(3.0, length / 60)
    for _ in range(2):
        mx, my = (x1 + x2) / 2, (y1 + y2) / 2
        nx, ny = (-(y2 - y1) / (length or 1), (x2 - x1) / (length or 1))
        off = rng.uniform(-bow, bow)
        paths.append(
            f"M{jitter(x1):.1f},{jitter(y1):.1f} Q{mx + nx * off:.1f},{my + ny * off:.1f} {jitter(x2):.1f},{jitter(y2):.1f}"
        )
    return " ".join(paths)


def rough_rect(x, y, w, h):
    r = 14
    pts = [(x + r, y), (x + w - r, y), (x + w, y + r), (x + w, y + h - r), (x + w - r, y + h), (x + r, y + h), (x, y + h - r), (x, y + r)]
    d = []
    for i in range(0, 8, 2):
        a, b = pts[i], pts[i + 1]
        d.append(rough_line(*a, *b))
        c = pts[(i + 2) % 8]
        corner = [(x + w, y), (x + w, y + h), (x, y + h), (x, y)][i // 2]
        d.append(f"M{b[0]:.1f},{b[1]:.1f} Q{corner[0]:.1f},{corner[1]:.1f} {c[0]:.1f},{c[1]:.1f}")
    return " ".join(d)


def esc(s):
    return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace('"', "&quot;")


def svg_text(x, y, w, h, text, size, align="center"):
    lines = text.split("\n")
    lh = size * 1.25
    top = y + (h - lh * len(lines)) / 2 + size * 0.95
    anchor, tx = ("middle", x + w / 2) if align == "center" else ("start", x)
    out = []
    for i, ln in enumerate(lines):
        out.append(f'<text x="{tx:.1f}" y="{top + i * lh:.1f}" font-size="{size}" text-anchor="{anchor}">{esc(ln)}</text>')
    return "\n".join(out)


def svg():
    font = base64.b64encode(FONT.read_bytes()).decode()
    parts = [
        f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {W} {H}" width="{W}" height="{H}">',
        "<defs><style>",
        f"@font-face {{ font-family: Virgil; src: url(data:font/woff2;base64,{font}) format('woff2'); }}",
        "text { font-family: Virgil, 'Segoe Print', 'Comic Sans MS', cursive; fill: #1e1e1e; }",
        "</style>",
        '<marker id="head" viewBox="0 0 12 12" refX="10" refY="6" markerWidth="12" markerHeight="12" orient="auto-start-reverse">'
        '<path d="M1,1 L10,6 L1,11" fill="none" stroke="#1e1e1e" stroke-width="1.8" stroke-linecap="round"/></marker>',
        "</defs>",
        f'<rect width="{W}" height="{H}" fill="#ffffff"/>',
    ]
    for id_, (x, y, w, h, label, (stroke, bg), dashed, size) in BOXES.items():
        if bg == "transparent" and label:
            parts.append(svg_text(x, y, w, h, label, size, "left" if id_.endswith("label") or id_ == "title" else "center"))
            continue
        if bg != "transparent":
            parts.append(f'<rect x="{x + 2}" y="{y + 2}" width="{w - 4}" height="{h - 4}" rx="12" fill="{bg}" opacity="0.75"/>')
        dash = ' stroke-dasharray="10 8"' if dashed else ""
        sw = 3 if id_ == "phone" else 2
        parts.append(f'<path d="{rough_rect(x, y, w, h)}" fill="none" stroke="{stroke}" stroke-width="{sw}" stroke-linecap="round"{dash}/>')
        if label:
            parts.append(svg_text(x, y, w, h, label, size))
    for a, b, label, dashed in ARROWS:
        x1, y1, x2, y2 = arrow_ends(a, b)
        dash = ' stroke-dasharray="10 8"' if dashed else ""
        parts.append(
            f'<path d="M{x1:.1f},{y1:.1f} Q{(x1 + x2) / 2 + rng.uniform(-6, 6):.1f},{(y1 + y2) / 2 + rng.uniform(-6, 6):.1f} {x2:.1f},{y2:.1f}" '
            f'fill="none" stroke="#1e1e1e" stroke-width="2" stroke-linecap="round" marker-end="url(#head)"{dash}/>'
        )
        if label:
            mx, my = (x1 + x2) / 2, (y1 + y2) / 2
            tw = len(label) * 8 + 12
            parts.append(f'<rect x="{mx - tw / 2:.1f}" y="{my - 12:.1f}" width="{tw}" height="22" fill="#ffffff"/>')
            parts.append(svg_text(mx - tw / 2, my - 12, tw, 22, label, 15))
    parts.append("</svg>")
    return "\n".join(parts)


if __name__ == "__main__":
    (HERE / "architecture.excalidraw").write_text(json.dumps(excalidraw(), indent=1, ensure_ascii=False) + "\n")
    (HERE / "architecture.svg").write_text(svg() + "\n")
    print("wrote architecture.excalidraw and architecture.svg")
