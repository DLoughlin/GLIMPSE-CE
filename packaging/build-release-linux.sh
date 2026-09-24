#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
CORRETTO_HOME="${CORRETTO_HOME:-}"
PACKAGE_TYPE="${PACKAGE_TYPE:-app-image}"
BUNDLE_NAME="${BUNDLE_NAME:-GLIMPSE-CE-portable}"
OUTPUT_DIR="${OUTPUT_DIR:-$SCRIPT_DIR/../dist}"
APP_NAME="${APP_NAME:-GLIMPSE-CE-ScenarioBuilder}"
APP_VERSION="${APP_VERSION:-8.2}"

PORTABLE_SCRIPT="$SCRIPT_DIR/package-portable-release.sh"
JPACKAGE_SCRIPT="$SCRIPT_DIR/package-jpackage-linux.sh"

if [ ! -f "$PORTABLE_SCRIPT" ]; then
  echo "Missing helper script: $PORTABLE_SCRIPT"
  exit 1
fi
if [ ! -f "$JPACKAGE_SCRIPT" ]; then
  echo "Missing helper script: $JPACKAGE_SCRIPT"
  exit 1
fi

if [ -z "$CORRETTO_HOME" ]; then
  echo "Set CORRETTO_HOME to a Corretto 21 Linux runtime/JDK root."
  exit 1
fi

echo "[1/2] Staging portable bundle with Corretto 21..."
CORRETTO_HOME="$CORRETTO_HOME" OUTPUT_DIR="$OUTPUT_DIR" BUNDLE_NAME="$BUNDLE_NAME" bash "$PORTABLE_SCRIPT"

PORTABLE_ROOT="$OUTPUT_DIR/$BUNDLE_NAME"
echo "[2/2] Building Linux package via jpackage ($PACKAGE_TYPE)..."
PORTABLE_ROOT="$PORTABLE_ROOT" OUTPUT_DIR="$OUTPUT_DIR/jpackage/linux" APP_NAME="$APP_NAME" APP_VERSION="$APP_VERSION" PACKAGE_TYPE="$PACKAGE_TYPE" bash "$JPACKAGE_SCRIPT"

echo "Release build completed successfully."
