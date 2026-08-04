#!/usr/bin/env bash
# Build (if needed) and start the MetaSec /sign server on :5099
set -euo pipefail
cd "$(dirname "$0")"
JAR="target/tiktok-metasec-encryption.jar"
PORT="${1:-5099}"

if [[ ! -f "$JAR" ]]; then
  echo "[*] Building shaded jar (first time takes a few minutes)…"
  mvn -q -DskipTests package
fi

echo "[*] Starting SignServer on http://0.0.0.0:${PORT}/sign"
exec java -Dmetasec.bind=0.0.0.0 -jar "$JAR" "$PORT"
