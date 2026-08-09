#!/bin/sh

set -eu

source_file="$1"
output_file="$2"
output_directory=$(dirname "$output_file")

mkdir -p "$output_directory"

exec xcrun clang \
  -dynamiclib \
  -fobjc-arc \
  -fblocks \
  -mmacosx-version-min=10.14 \
  -arch arm64 \
  -arch x86_64 \
  -framework Foundation \
  -framework UserNotifications \
  "$source_file" \
  -o "$output_file"
