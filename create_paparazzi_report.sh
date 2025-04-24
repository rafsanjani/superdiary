#!/bin/bash

# Commit failure images to github
git config --global user.name "GitHub Actions"
git config --global user.email "actions@github.com"
git checkout --orphan "paparazzi-snapshots-$BUILD_NUMBER"
git reset
find . -path '*/build/paparazzi/failures/delta-*' -exec cp -R {} . \;
git add delta-*
git commit -m "Upload paparazzi failures"
git push --force origin "paparazzi-snapshots-$BUILD_NUMBER"

# Create a markdown file containing all the error images
OUTPUT_FILE="snapshots.md" > "$OUTPUT_FILE"

# Environment variable for the pull request number
PULL_REQUEST_NUMBER="${BUILD_NUMBER}"

# Find all delta* files inside any paparazzi/failures directory
find . -type f -path "*/build/paparazzi/failures/delta*" | while read -r file; do
  FILENAME=$(basename "$file")
  ENCODED_FILENAME=${FILENAME//'['/'%5B'}
  ENCODED_FILENAME=${ENCODED_FILENAME//']'/'%5D'}

  HEADER="#### $FILENAME"
  IMAGE_TAG="<img alt=\"paparazzi failure\" src=\"https://github.com/rafsanjani/superdiary/raw/paparazzi-snapshots-${PULL_REQUEST_NUMBER}/${ENCODED_FILENAME}\"/>"

  echo -e "$HEADER\n$IMAGE_TAG\n" >> "$OUTPUT_FILE"
done


cat snapshots.md
