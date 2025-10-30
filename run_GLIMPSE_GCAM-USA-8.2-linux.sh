#!/bin/bash
# If the following command does not start the ScenarioBuilder, correct the JAVA_HOME path

export JAVA_HOME="./amazon-corretto-8.462.08.1-linux-x64"

if [ ! -f "$JAVA_HOME/bin/java" ]; then
  echo "JAVA_HOME setting needs to be fixed"
  read -p "Press enter to continue..."
  exit 1
fi

export JAVA_JVM_PATH="$JAVA_HOME/bin/server":"$JAVA_HOME/jre/bin"
export PATH=".:$JAVA_JVM_PATH:$JAVA_HOME:$JAVA_HOME/bin:../../ModelInterface:$PATH"

java -Dprism.order=sw -jar ./GLIMPSE-ScenarioBuilder/GLIMPSE-ScenarioBuilder.jar -options options_GCAM-USA-8.2-linux.txt

exit 0
