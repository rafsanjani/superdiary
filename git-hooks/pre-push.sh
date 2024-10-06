#!/bin/sh

echo "Running static analysis."

./gradlew ktlintCheck
