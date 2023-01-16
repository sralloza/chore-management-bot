#!/usr/bin/env bash
set -e

: ${GITHUB_TOKEN:?Must set \$GITHUB_TOKEN}

export API_VERSION=$(curl -s https://api.github.com/repos/sralloza/chore-management-api/releases/latest -H "Authorization: Bearer $GITHUB_TOKEN" | jq -r ".tag_name" | sed -e "s/v//g")
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
SCRIPT_DIR=$(dirname -- $SCRIPT_DIR)

echo "Using API version $API_VERSION"

docker-compose up
