#!/usr/bin/env bash
# Installs the Android toolchain this repository needs to build an APK.
# Idempotent: re-running skips anything already present.
set -euo pipefail

SDK_ROOT="${ANDROID_SDK_ROOT:-/home/alan/android-sdk}"
# Pinned so a changed upstream zip cannot silently alter builds.
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip"
CMDLINE_TOOLS_SHA256="7ec965280a073311c339e571cd5de778b9975026cfcbe79f2b1cdcb1e15317ee"

# compileSdk is 37; AGP also resolves build-tools 36 for its own tasks.
SDK_PACKAGES=(
  "platform-tools"
  "platforms;android-37.0"
  "build-tools;37.0.0"
  "build-tools;36.0.0"
)

if [ ! -x "$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" ]; then
  echo "Installing Android command-line tools into $SDK_ROOT"
  tmp_zip="$(mktemp -d)/cmdtools.zip"
  curl -fsSL -o "$tmp_zip" "$CMDLINE_TOOLS_URL"
  echo "$CMDLINE_TOOLS_SHA256  $tmp_zip" | sha256sum -c -
  mkdir -p "$SDK_ROOT/cmdline-tools"
  unzip -q "$tmp_zip" -d "$SDK_ROOT/cmdline-tools"
  rm -rf "$SDK_ROOT/cmdline-tools/latest"
  mv "$SDK_ROOT/cmdline-tools/cmdline-tools" "$SDK_ROOT/cmdline-tools/latest"
fi

export ANDROID_SDK_ROOT="$SDK_ROOT"
export ANDROID_HOME="$SDK_ROOT"
export PATH="$SDK_ROOT/cmdline-tools/latest/bin:$SDK_ROOT/platform-tools:$PATH"

yes | sdkmanager --licenses > /dev/null
sdkmanager --install "${SDK_PACKAGES[@]}" > /dev/null

# Point Gradle at the SDK without relying on an exported environment variable.
repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
echo "sdk.dir=$SDK_ROOT" > "$repo_root/apps/android/local.properties"

echo "Android SDK ready at $SDK_ROOT"
java -version 2>&1 | head -1
