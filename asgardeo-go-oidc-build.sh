#!/usr/bin/env bash

# -------------------------------------------------------------------------------------
#
# Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 LLC. and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#
# --------------------------------------------------------------------------------------

# Fail the script when a subsuquent command or pipe redirection fails
set -e
set -o pipefail

# Variables
REPO_DIR=$1
GIT_TOKEN=$2
VERSION=$(cat version.txt)
GIT_USERNAME='wso2-iam-cloud-bot'

# Check relevant packages are available
command -v gh >/dev/null 2>&1 || { echo >&2 "Error: $0 script requires 'gh' to call GitHub APIs.  Aborting as not found."; exit 1; }
command -v go >/dev/null 2>&1 || { echo >&2 "Error: $0 script requires 'go' to build the artifact.  Aborting as not found."; exit 1; } 

# Go Build command.
go build -o outbound-server server/server.go

echo "Creating release for version - "$VERSION
echo $GIT_TOKEN | gh auth login --with-token
gh release create --title $VERSION $REPO_DIR/outbound-server

# Method to increment minor version
incrementPackVersion() {
    old_version=$1
    echo "$old_version" | awk -F. '{print $1"."$2"."$3+1}'
}

NEW_OIDC_OUTBOUND_GO_SERVER_VERSION=$(incrementPackVersion "$VERSION")
echo "Next OIDC Outbound Server in Go lang version: $NEW_OIDC_OUTBOUND_GO_SERVER_VERSION"

# Update version.txt file.
sed -i 's|'"${VERSION}"'|'"${NEW_ASGARDEO_DOCS_VERSION}"'|' version.txt

#Commit and push updated version.txt file.
git -C "$REPO_DIR" config user.name "$GIT_USERNAME"
git -C "$REPO_DIR" config user.email "iam-cloud@wso2.com"
git -C "$REPO_DIR" pull

git -C "$REPO_DIR" add "version.txt"
git -C "$REPO_DIR" commit -m "Updating version to $NEW_ASGARDEO_DOCS_VERSION"
git -C "$REPO_DIR" push