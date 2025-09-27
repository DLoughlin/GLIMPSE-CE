#!/bin/bash
# If the following command does not start the ModelInterface, correct the JAVA_HOME path

# Set these variables
JAVA_HOME="../amazon-corretto-8.462.08.1-linux-x64"

# Checking JAVA_HOME setup
if [ ! -f "$JAVA_HOME/bin/java" ]; then
  echo "JAVA_HOME setting needs to be fixed"
  read -p "Press enter to continue..."
  exit 1
fi

JAVA_JVM_PATH="$JAVA_HOME/bin/server"
export PATH=".:$JAVA_JVM_PATH:$JAVA_HOME:$JAVA_HOME/bin:$PATH"

java -jar ./GLIMPSE-ModelInterface.jar

exit 0
