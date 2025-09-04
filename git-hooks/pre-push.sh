#!/bin/sh
set -e
echo "Running static analysis."
# download ktlint cli with curl if it doesn't exist
if [ ! -f ktlint ]; then
    echo "Downloading ktlint..."
    curl -sSL https://github.com/pinterest/ktlint/releases/download/1.7.1/ktlint -o ktlint
    chmod +x ktlint
fi
# download compose rules with curl if it doesn't exist
if [ ! -f compose-rules.jar ]; then
    echo "Downloading compose-rules..."
    curl -sSL https://github.com/mrmans0n/compose-rules/releases/download/v0.4.27/ktlint-compose-0.4.27-all.jar -o compose-rules.jar
fi
./ktlint --ruleset=compose-rules.jar '**/*.kt' '**/*.kts' '!**/build/**'
