#!/bin/sh

######## DETEKT-GRADLE HOOK START ########

CHANGED_FILES="$(git --no-pager diff --name-status --no-color --cached | awk '$1 != "D" && $2 ~ /\.kts|\.kt/ { print $2}')"

if [ -z "$CHANGED_FILES" ]; then
  echo "No Kotlin staged files."
  exit 0
fi

echo "Running detekt"
echo "$CHANGED_FILES"

/gradlew androidApp:detekt

echo "Completed detekt run."

echo "$CHANGED_FILES" | while read -r file; do
  if [ -f "$file" ]; then
    git add "$file"
  fi
done

######## DETEKT-GRADLE HOOK END ########
