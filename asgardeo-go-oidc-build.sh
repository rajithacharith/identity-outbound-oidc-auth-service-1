#!/usr/bin/env bash

# Fail the script when a subsuquent command or pipe redirection fails
set -e
set -o pipefail

# Variables
REPO_DIR=$1
GIT_TOKEN=$2
git fetch --tags
# This suppress an error occurred when the repository is a complete one.
git fetch --prune --unshallow || true
latest_tag=$(git describe --abbrev=0 --tags || true)
echo latest_tag
echo $(ls)
echo $(git describe --tags --abbrev=0 --always)
cd $REPO_DIR
echo $(ls)
VERSION=$(echo $(git describe --tags --abbrev=0 --always))
ASGARDEO_DOCS_NAME=asgardeo-docs-$VERSION
GIT_USERNAME=''

# Check relevant packages are available
command -v jq >/dev/null 2>&1 || { echo >&2 "Error: $0 script requires 'jq' for JSON Processing.  Aborting as not found."; exit 1; }
command -v gh >/dev/null 2>&1 || { echo >&2 "Error: $0 script requires 'gh' to call GitHub APIs.  Aborting as not found."; exit 1; } 