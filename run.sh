#!/bin/bash
set -e
cd "$(dirname "$0")"
PORT=${1:-5099}
JAR=target/tiktok-metasec-encryption.jar

if [ ! -f "$JAR" ]; then
  mvn -q -DskipTests package
fi

exec java -Dmetasec.bind=0.0.0.0 -jar "$JAR" "$PORT"
