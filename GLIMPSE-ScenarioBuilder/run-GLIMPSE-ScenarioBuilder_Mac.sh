#!/bin/bash
set -euo pipefail

# Launch GLIMPSE ScenarioBuilder on macOS using Java 21+ and the Mac JavaFX runtime jars.

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAVA_BIN=""
JAVA_OS_ARCH="$(uname -m 2>/dev/null || echo "")"
if [[ "$JAVA_OS_ARCH" == "arm64" || "$JAVA_OS_ARCH" == "aarch64" ]]; then
  JAVAFX_DIR="$SCRIPT_DIR/libs/javafx-21/mac-aarch64"
  JAVAFX_SUFFIX="mac-aarch64"
else
  JAVAFX_DIR="$SCRIPT_DIR/libs/javafx-21/mac"
  JAVAFX_SUFFIX="mac"
fi

if [[ -n "${JAVA_HOME:-}" && -x "$JAVA_HOME/bin/java" ]]; then
  JAVA_BIN="$JAVA_HOME/bin/java"
elif [[ -x /usr/libexec/java_home ]]; then
  MAC_JAVA_HOME="$(/usr/libexec/java_home -v 21 2>/dev/null || true)"
  if [[ -n "$MAC_JAVA_HOME" && -x "$MAC_JAVA_HOME/bin/java" ]]; then
    JAVA_BIN="$MAC_JAVA_HOME/bin/java"
  fi
fi

if [[ -z "$JAVA_BIN" ]] && command -v java >/dev/null 2>&1; then
  JAVA_BIN="$(command -v java)"
fi

if [[ -z "$JAVA_BIN" ]]; then
  echo "Could not find java. Set JAVA_HOME to a Java 21+ installation or add java to PATH."
  exit 1
fi

if [[ ! -f "$JAVAFX_DIR/javafx-controls-21.0.4-$JAVAFX_SUFFIX.jar" ]]; then
  echo "Missing JavaFX runtime jars in $JAVAFX_DIR"
  echo "Expected javafx-21/mac or javafx-21/mac-aarch64 jars under GLIMPSE-ScenarioBuilder/libs."
  exit 1
fi

"$JAVA_BIN" -Dprism.order=sw \
  --module-path "$JAVAFX_DIR" \
  --add-modules javafx.controls,javafx.fxml \
  --add-exports=javafx.base/com.sun.javafx.runtime=ALL-UNNAMED \
  --add-exports=javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED \
  -jar "$SCRIPT_DIR/GLIMPSE-ScenarioBuilder.jar" \
  -options "$SCRIPT_DIR/GLIMPSE-ScenarioBuilder.properties"
