#!/bin/sh
# Build and run BloodLink. Set BLOODLINK_DB_PASSWORD (and optionally BLOODLINK_DB_URL / BLOODLINK_DB_USER).
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -d out $(find src -name "*.java")
exec java -cp out:lib/mysql-connector-j.jar com.bloodlink.Main
