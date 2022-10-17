#!/usr/bin/env bash

# Fail the script when a subsuquent command or pipe redirection fails
set -e
set -o pipefail

# Variables
REPO_DIR=$1
GIT_TOKEN=$2

# Check relevant packages are available
command -v jq >/dev/null 2>&1 || { echo >&2 "Error: $0 script requires 'jq' for JSON Processing.  Aborting as not found."; exit 1; }
command -v gh >/dev/null 2>&1 || { echo >&2 "Error: $0 script requires 'gh' to call GitHub APIs.  Aborting as not found."; exit 1; } 

VERSION=$(cat version.txt)
echo $VERSION
GIT_USERNAME='wso2-iam-cloud-bot'

incrementPackVersion() {
    old_version=$1
    echo "$old_version" | awk -F. '{print $1"."$2"."$3+1}'
}

NEW_ASGARDEO_DOCS_VERSION=$(incrementPackVersion "$VERSION")
echo "Next asgardeo-docs version: $NEW_ASGARDEO_DOCS_VERSION"

sed -i 's|'"${VERSION}"'|'"${NEW_ASGARDEO_DOCS_VERSION}"'|' version.txt

git -C "$REPO_DIR" config user.name "$GIT_USERNAME"
git -C "$REPO_DIR" config user.email "iam-cloud@wso2.com"
git -C "$REPO_DIR" pull

git -C "$REPO_DIR" add "version.txt"
git -C "$REPO_DIR" commit -m "Updating version to $NEW_ASGARDEO_DOCS_VERSION"
git -C "$REPO_DIR" push