#!/usr/bin/env bash
set -e

: ${GITHUB_TOKEN:?Must set \$GITHUB_TOKEN}

export API_VERSION=$(curl -s https://api.github.com/repos/sralloza/chore-management-api/releases/latest -H "Authorization: Bearer $GITHUB_TOKEN" | jq -r ".tag_name" | sed -e "s/v//g")

echo "Using API version $API_VERSION"

docker-compose up
