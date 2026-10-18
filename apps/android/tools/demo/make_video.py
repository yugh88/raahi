"""Builds the Raahi demo video: each screen recording beside a caption panel, plus title cards.

Long still stretches (waiting on scripted taps) are shortened to HOLD seconds; nothing else is
edited, sped up or reordered.
"""
import re
import subprocess
import sys
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

ROOT = Path(sys.argv[1])  # DEMO_DIR: holds video/ with the recorded segments
V = ROOT / "video"
OUT = V / "build"
OUT.mkdir(exist_ok=True)
W, H = 1280, 720
PHONE_W, PHONE_H = 336, 672
HOLD = 2.5
FONT = "/System/Library/Fonts/Supplemental/Arial.ttf"
BOLD = "/System/Library/Fonts/Supplemental/Arial Bold.ttf"
INK, MUTED, NEEL, GOLD, LEAF = (26, 31, 54), (90, 98, 120), (35, 64, 168), (168, 111, 0), (31, 107, 61)

SEGMENTS = [
    ("s1", "1  Pick a language, meet Didi", [
        "Five languages are bundled — Hindi, Tamil, Telugu, Kannada, Malayalam — each with its own font.",
        "Onboarding is five short questions, one per screen, with big picture tiles.",
        "The profile is saved in an encrypted database on the phone (SQLCipher, key wrapped by Android Keystore).",
    ]),
    ("s2", "2  Today's lesson", [
        "Class 10 Science, Electricity (NCERT chapter): 12 questions, 6 topics.",
        "A wrong answer is not a mark lost: it tells Didi which 'trap' (misconception) to work on.",
        "The questions were written by the Raahi team; teacher review is pending, and every question says so.",
    ]),
    ("s3", "3  Trap test: no score, only traps", [
        "The first answer is given by voice — \"pahla\" (\"the first one\") — and recognised on the phone by Vosk (small Hindi model, offline).",
        "In this recording a spoken Hindi clip is fed from a file to the same recogniser, because the emulator has no microphone.",
        "Six answers, three traps found. Each trap gets an explanation and a retry.",
    ]),
    ("s4", "4  Untangle a trap", [
        "Didi reads the explanation line by line in the phone's offline Hindi voice; the highlighted line follows her. (This recording has no audio.)",
        "The retry uses a different question that tests the same idea. Two right in a row clears the trap.",
        "A spaced re-check is scheduled (1, 3, 7, 21 days) and the climber moves up the mountain.",
    ]),
    ("s5", "5  Which scholarships can I get?", [
        "Didi asks at most 6 questions, each chosen by how many schemes its answer decides.",
        "Three are answered by voice: \"college degree\", \"Madhya Pradesh\", \"the second\" (income band).",
        "Income and caste are stored as bands, on this phone only.",
        "Verdicts come from 10 real schemes. Each reason quotes the official rule, with its source and the date it was checked.",
        "The guide gives the official link and safe draft fields to copy. It never asks for Aadhaar, OTP, bank details or passwords.",
    ]),
    ("s6", "6  Ask Didi — offline, sourced, honest", [
        "\"Ohm ka niyam\" asked by voice: the answer comes from the lesson, with its NCERT source.",
        "A question outside what she knows gets \"I don't know yet — ask your teacher\". She never guesses.",
        "Progress shows diyas, a streak and badges, and no individual ranks.",
    ]),
    ("s7", "7  The same app in Tamil", [
        "The interface switches language instantly. The Tamil, Telugu, Kannada and Malayalam text is drafted and waiting for native-speaker review.",
        "The lesson pack is Hindi and English for now, so the lesson name shows in English. That is the next pack to translate.",
    ]),
    ("s8", "8  Added overnight", [
        "Spaced re-check: a cleared trap comes back when due (1, 3, 7, 21 days), answered here by voice (\"doosra\"). For the demo it was made due with a debug switch, not by waiting a day.",
        "Ask Didi takes free speech: the whole question is transcribed on the phone, then matched to the lesson.",
        "Days left from each official deadline. A \"maybe\" names the missing question; answering two more makes MMVY eligible.",
        "In the guide, saying \"ho gaya\" moves to the next step. Easy reading (shown on Progress) has Didi read every option aloud.",
    ]),
]

INTRO = ("Raahi", "Didi — a voice-first guide to learning and scholarships, built for low-end Android phones", [
    "Recorded 19 October 2026 on an Android emulator set up like a low-end phone: 2 GB RAM, 720 × 1440, Android 15.",
    "Airplane mode is on for the whole recording. The app has no INTERNET permission at all.",
    "Debug build: the grey strip at the top opens a list of every screen for testing.",
])

OUTRO = ("What is real, what is next", None, [
    "REAL TODAY",
    "• Learning loop: trap test → explanation → retry → cleared → spaced re-check, saved in an encrypted on-phone database.",
    "• 11 schemes with rules from official documents (central, MP, TN, KA, TG, KL); adaptive questions, ≤ 6 at first.",
    "• Offline Hindi voice on every core step (Vosk + phone TTS), spaced re-checks, easy reading. Release APK 14.9 MB. 200+ tests on real rules.",
    "NOT BUILT YET (said honestly in the app and in docs/)",
    "• Backend, login and sync; teacher dashboard; courses and payments; events. Those screens are labelled design reference.",
    "• No scheme rules yet for Andhra Pradesh (official page unreadable). Lesson video and ta/te/kn/ml lesson text are also missing.",
    "Code and docs: github.com/yugh88/raahi",
])


def font(size, bold=False):
    return ImageFont.truetype(BOLD if bold else FONT, size)


def wrap(draw, text, f, width):
    words, lines, line = text.split(), [], ""
    for w in words:
        t = (line + " " + w).strip()
        if draw.textlength(t, font=f) <= width:
            line = t
        else:
            lines.append(line)
            line = w
    lines.append(line)
    return lines


def panel(title, lines, path, phone=True):
    im = Image.new("RGB", (W, H), (247, 248, 251))
    d = ImageDraw.Draw(im)
    x = 470 if phone else 90
    width = W - x - 70
    if phone:
        d.rounded_rectangle((60, 14, 60 + PHONE_W + 20, 14 + PHONE_H + 20), 22, fill=(26, 31, 54))
    y = 70
    d.text((x, y), title, font=font(38, True), fill=NEEL)
    y += 70
    for text in lines:
        heading = text.split(" (")[0].isupper()
        f = font(22, heading) if heading else font(24)
        for ln in wrap(d, text, f, width):
            d.text((x, y), ln, font=f, fill=GOLD if heading else INK)
            y += 34
        y += 14
    d.text((x, H - 50), "Raahi · Citi India Hackathon demo · offline, Hindi", font=font(18), fill=MUTED)
    im.save(path)


def freezes(src):
    log = subprocess.run(["ffmpeg", "-hide_banner", "-i", str(src), "-vf", "freezedetect=n=0.003:d=3", "-map", "0:v", "-f", "null", "-"],
                         capture_output=True, text=True).stderr
    starts = [float(x) for x in re.findall(r"freeze_start: ([\d.]+)", log)]
    ends = [float(x) for x in re.findall(r"freeze_end: ([\d.]+)", log)]
    return list(zip(starts, ends))


def run(*args):
    subprocess.run(["ffmpeg", "-hide_banner", "-loglevel", "error", "-y", *args], check=True)


def segment(name, title, lines, i):
    src = V / f"{name}.mp4"
    cuts = [(s + HOLD, e) for s, e in freezes(src) if e - s > HOLD + 0.5]
    keep = "+".join(f"between(t,{a:.3f},{b:.3f})" for a, b in cuts)
    select = f"select='not({keep})'," if keep else ""
    bg = OUT / f"bg{i}.png"
    panel(title, lines, bg)
    out = OUT / f"part{i}.mp4"
    run("-loop", "1", "-i", str(bg), "-i", str(src), "-filter_complex",
        f"[1:v]fps=30,{select}setpts=N/30/TB,scale={PHONE_W}:{PHONE_H}[p];[0:v][p]overlay=70:24:shortest=1,format=yuv420p[v]",
        "-map", "[v]", "-r", "30", "-c:v", "libx264", "-preset", "medium", "-crf", "20", str(out))
    return out


def card(title, subtitle, lines, i, seconds):
    bg = OUT / f"card{i}.png"
    im = Image.new("RGB", (W, H), (247, 248, 251))
    d = ImageDraw.Draw(im)
    y = 90
    d.text((90, y), title, font=font(52, True), fill=NEEL)
    y += 80
    if subtitle:
        for ln in wrap(d, subtitle, font(30), W - 180):
            d.text((90, y), ln, font=font(30), fill=INK)
            y += 42
        y += 30
    for text in lines:
        heading = text.split(" (")[0].isupper()
        f = font(23, True) if heading else font(23)
        for ln in wrap(d, text, f, W - 180):
            d.text((90, y), ln, font=f, fill=GOLD if heading else (LEAF if text.startswith("Code") else INK))
            y += 32
        y += 12
    im.save(bg)
    out = OUT / f"card{i}.mp4"
    run("-loop", "1", "-i", str(bg), "-t", str(seconds), "-r", "30", "-c:v", "libx264", "-pix_fmt", "yuv420p", "-crf", "20", str(out))
    return out


parts = [card(*INTRO, 0, 9)]
for i, (name, title, lines) in enumerate(SEGMENTS, start=1):
    parts.append(segment(name, title, lines, i))
parts.append(card(*OUTRO, 9, 16))
listing = OUT / "list.txt"
listing.write_text("".join(f"file '{p}'\n" for p in parts))
final = V / "raahi-demo.mp4"
run("-f", "concat", "-safe", "0", "-i", str(listing), "-c", "copy", "-movflags", "+faststart", str(final))
print(final)
