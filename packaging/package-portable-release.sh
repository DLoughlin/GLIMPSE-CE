#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
CORRETTO_HOME="${CORRETTO_HOME:-}"
OUTPUT_DIR="${OUTPUT_DIR:-$REPO_ROOT/dist}"
BUNDLE_NAME="${BUNDLE_NAME:-GLIMPSE-CE-portable}"

if [ -z "$CORRETTO_HOME" ] || [ ! -x "$CORRETTO_HOME/bin/java" ]; then
  echo "Set CORRETTO_HOME to a Corretto 21 Linux runtime or JDK that contains bin/java."
  exit 1
fi

STAGE_ROOT="$OUTPUT_DIR/$BUNDLE_NAME"
LINUX_RUNTIME_ROOT="$STAGE_ROOT/runtime/corretto-21/linux-x64"

required_items=(
  "GLIMPSE-ScenarioBuilder"
  "GLIMPSE-ModelInterface"
  "run_GLIMPSE_GCAM-USA-8.2-windows.bat"
  "run_GLIMPSE_GCAM-global-8.2-windows.bat"
  "run_GLIMPSE_GCAM-USA-8.2-linux.sh"
  "run_GLIMPSE_GCAM-global-8.2-linux.sh"
  "options_GCAM-USA-8.2-windows.txt"
  "options_GCAM-global-8.2-windows.txt"
  "options_GCAM-USA-8.2-linux.txt"
  "options_GCAM-global-8.2-linux.txt"
)

rm -rf "$STAGE_ROOT"
mkdir -p "$STAGE_ROOT"

for item in "${required_items[@]}"; do
  if [ ! -e "$REPO_ROOT/$item" ]; then
    echo "Missing required release input: $REPO_ROOT/$item"
    exit 1
  fi
  cp -R "$REPO_ROOT/$item" "$STAGE_ROOT/"
done

mkdir -p "$LINUX_RUNTIME_ROOT"
cp -R "$CORRETTO_HOME"/. "$LINUX_RUNTIME_ROOT/"

mkdir -p "$OUTPUT_DIR"
tar -C "$OUTPUT_DIR" -czf "$OUTPUT_DIR/$BUNDLE_NAME-linux-x64.tar.gz" "$BUNDLE_NAME"

echo "Created portable release: $OUTPUT_DIR/$BUNDLE_NAME-linux-x64.tar.gz"
echo "Bundle root: $STAGE_ROOT"
echo "Bundled runtime expected by launchers: runtime/corretto-21/linux-x64"
