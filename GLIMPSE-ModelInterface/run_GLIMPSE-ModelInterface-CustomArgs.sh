#!/bin/bash
# If the following command does not start the ModelInterface, correct the JAVA_HOME path

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Set these variables; comment out line to use default
JAVA_HOME="$SCRIPT_DIR/../amazon-corretto-8.442.06.1-windows-x64-jre"
QUERY_FILE="$SCRIPT_DIR/config/Main_queries_GLIMPSE-7p0.xml"
DATABASE="$SCRIPT_DIR/../../GCAM-Model/gcam-v7.0/output/database"
UNITS="$SCRIPT_DIR/config/units_rules.csv"
FAVORITES="$SCRIPT_DIR/config/favorite_queries_list.txt"
REGIONS="$SCRIPT_DIR/config/preset_regions_list.txt"
MAPS="$SCRIPT_DIR/map_resources/AllMapInfo"

# Checking JAVA_HOME setup
if [ ! -f "$JAVA_HOME/bin/java" ]; then
  echo "JAVA_HOME setting needs to be fixed"
  read -p "Press enter to continue..."
  exit 1
fi

JAVA_JVM_PATH="$JAVA_HOME/bin/server"
export PATH=".:$JAVA_JVM_PATH:$JAVA_HOME:$JAVA_HOME/bin:$PATH"

java -jar "$SCRIPT_DIR/GLIMPSE-ModelInterface.jar" -q "$QUERY_FILE" -o "$DATABASE" -u "$UNITS" -f "$FAVORITES" -p "$REGIONS" -m "$MAPS"

exit 0
