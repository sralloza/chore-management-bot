#!/usr/bin/env bash
: ${GITHUB_TOKEN:?Must set \$GITHUB_TOKEN}


export API_VERSION=$(curl -s https://api.github.com/repos/sralloza/chore-management-api/releases/latest -H "Authorization: Bearer $GITHUB_TOKEN" | jq -r ".tag_name" | sed -e "s/v//g")
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
SCRIPT_DIR=$(dirname -- $SCRIPT_DIR)

echo "Using API version $API_VERSION"
echo "Getting secrets"

cd "$SCRIPT_DIR"

ansible-vault decrypt --vault-pass-file .vault-pass.txt --output environment.json  settings/environment.json
export TELEGRAM_BOT_TOKEN=$(cat environment.json | jq -r ".bot_token")
export TELEGRAM_BOT_USERNAME=$(cat environment.json | jq -r ".bot_username")
export TELEGRAM_CREATOR_ID=$(cat environment.json | jq -r ".telegram_creator_id")
export ADMIN_TOKEN=$(cat environment.json | jq -r ".admin_token")
rm environment.json

docker-compose up -d "$@"
