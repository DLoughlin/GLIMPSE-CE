#!/bin/bash
# If the following command does not start the ScenarioBuilder, correct the JAVA_HOME path

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
export JAVA_HOME="$SCRIPT_DIR/amazon-corretto-8.442.06.1-windows-x64-jre"

if [ ! -f "$JAVA_HOME/bin/java" ]; then
  echo "JAVA_HOME setting needs to be fixed"
  read -p "Press enter to continue..."
  exit 1
fi

export JAVA_JVM_PATH="$JAVA_HOME/bin/server"
export PATH=".:$JAVA_JVM_PATH:$JAVA_HOME:$JAVA_HOME/bin:../../ModelInterface:$PATH"

java -Dprism.order=sw -jar ./GLIMPSE-ScenarioBuilder/GLIMPSE-ScenarioBuilder.jar -options options_GCAM-global-8.2.txt

exit 0
