#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
PORTABLE_ROOT="${PORTABLE_ROOT:-}"
OUTPUT_DIR="${OUTPUT_DIR:-$REPO_ROOT/dist/jpackage/linux}"
APP_NAME="${APP_NAME:-GLIMPSE-CE-ScenarioBuilder}"
APP_VERSION="${APP_VERSION:-8.2}"
PACKAGE_TYPE="${PACKAGE_TYPE:-app-image}"

if [ -z "$PORTABLE_ROOT" ]; then
  echo "Set PORTABLE_ROOT to the staged portable bundle root."
  exit 1
fi

if ! command -v jpackage >/dev/null 2>&1; then
  echo "jpackage is not available on PATH."
  exit 1
fi

if ! command -v tar >/dev/null 2>&1; then
  echo "tar is not available on PATH."
  exit 1
fi

PORTABLE_ROOT="$(cd "$PORTABLE_ROOT" && pwd)"
RUNTIME_IMAGE="$PORTABLE_ROOT/runtime/corretto-21/linux-x64"
if [ ! -x "$RUNTIME_IMAGE/bin/java" ]; then
  echo "PortableRoot must contain the bundled Corretto 21 runtime at runtime/corretto-21/linux-x64"
  exit 1
fi

STAGING_ROOT="$OUTPUT_DIR/staging"
INPUT_ROOT="$STAGING_ROOT/input"
PACKAGE_ROOT="$STAGING_ROOT/package"
ICON_PATH="$REPO_ROOT/resources/GLIMPSE_icon.png"

rm -rf "$STAGING_ROOT"
mkdir -p "$INPUT_ROOT" "$PACKAGE_ROOT" "$OUTPUT_DIR"

cp -R "$PORTABLE_ROOT/GLIMPSE-ScenarioBuilder" "$INPUT_ROOT/"
cp "$PORTABLE_ROOT/GLIMPSE-ScenarioBuilder/GLIMPSE-ScenarioBuilder.jar" "$INPUT_ROOT/"

if [ -d "$PORTABLE_ROOT/GLIMPSE-ModelInterface" ]; then
  cp -R "$PORTABLE_ROOT/GLIMPSE-ModelInterface" "$INPUT_ROOT/"
fi

for item in \
  options_GCAM-USA-8.2-windows.txt \
  options_GCAM-global-8.2-windows.txt \
  options_GCAM-USA-8.2-linux.txt \
  options_GCAM-global-8.2-linux.txt \
  model_interface.properties
  do
  if [ -e "$PORTABLE_ROOT/$item" ]; then
    cp "$PORTABLE_ROOT/$item" "$INPUT_ROOT/"
  fi
done

JAVA_OPTIONS=(
  -Dprism.order=sw
  --module-path '$APPDIR/GLIMPSE-ScenarioBuilder/libs/javafx-21/linux'
  --add-modules=javafx.base,javafx.graphics,javafx.controls,javafx.fxml
  --add-exports=javafx.base/com.sun.javafx.runtime=ALL-UNNAMED
  --add-exports=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED
)

JPACKAGE_ARGS=(
  --type "$PACKAGE_TYPE"
  --dest "$PACKAGE_ROOT"
  --input "$INPUT_ROOT"
  --name "$APP_NAME"
  --app-version "$APP_VERSION"
  --main-jar GLIMPSE-ScenarioBuilder.jar
  --main-class org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader
  --runtime-image "$RUNTIME_IMAGE"
)

if [ "$PACKAGE_TYPE" != "app-image" ]; then
  JPACKAGE_ARGS+=(--linux-shortcut)
fi

if [ -f "$ICON_PATH" ]; then
  JPACKAGE_ARGS+=(--icon "$ICON_PATH")
fi

for opt in "${JAVA_OPTIONS[@]}"; do
  JPACKAGE_ARGS+=(--java-options "$opt")
done

jpackage "${JPACKAGE_ARGS[@]}"

if [ "$PACKAGE_TYPE" = "app-image" ]; then
  IMAGE_DIR="$PACKAGE_ROOT/$APP_NAME"
  TAR_PATH="$OUTPUT_DIR/$APP_NAME-linux-x64-app-image.tar.gz"
  rm -f "$TAR_PATH"
  tar -C "$PACKAGE_ROOT" -czf "$TAR_PATH" "$APP_NAME"
  echo "Created app image: $IMAGE_DIR"
  echo "Created archive: $TAR_PATH"
else
  find "$PACKAGE_ROOT" -maxdepth 1 -type f -print | sed 's/^/Created output: /'
fi
