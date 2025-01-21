#!/bin/sh
git config --global user.name "GitHub Actions"
git config --global user.email "actions@github.com"
git checkout --orphan "paparazzi-snapshots-$BUILD_NUMBER"
git reset
find . -path '*/build/paparazzi/failures/delta-*' -exec cp -R {} . \;
git add delta-*
git commit -m "Upload paparazzi failures"
git push --force origin "paparazzi-snapshots-$BUILD_NUMBER"
