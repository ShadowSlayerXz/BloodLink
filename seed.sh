#!/bin/sh
# Load synthetic demo data (accounts, blood banks, sample requests). Safe to re-run.
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -d out $(find src -name "*.java")
[ "$(mysql -uroot -N -e 'select count(*) from bloodlink_db.blood_banks')" = "0" ] && mysql -uroot < database/sample_data.sql
java -cp out:lib/mysql-connector-j.jar com.bloodlink.SeedData
