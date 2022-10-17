#!/usr/bin/env bash

# Fail the script when a subsuquent command or pipe redirection fails
set -e
set -o pipefail

# Variables
REPO_DIR=$1
GIT_TOKEN=$2
echo $(ls)
cd $REPO_DIR
echo $(ls)
VERSION=$(echo $(git describe --tags --abbrev=0))
ASGARDEO_DOCS_NAME=asgardeo-docs-$VERSION
GIT_USERNAME='wso2-iam-cloud-bot'

# Check relevant packages are available
command -v jq >/dev/null 2>&1 || { echo >&2 "Error: $0 script requires 'jq' for JSON Processing.  Aborting as not found."; exit 1; }
command -v gh >/dev/null 2>&1 || { echo >&2 "Error: $0 script requires 'gh' to call GitHub APIs.  Aborting as not found."; exit 1; } 