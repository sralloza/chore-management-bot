#!/usr/bin/env bash

set -ueo pipefail

baseURL="${1:-http://localhost:8080}"
apiKey=$(cat .env | grep ADMIN_API_KEY | cut -d '=' -f2)
userId=$(cat .env | grep TELEGRAM_CREATOR_ID | cut -d '=' -f2)

echo "+Creating users"
curl -H "x-token: $apiKey" -H 'Content-Type: application/json' --data '{"id":"'$userId'","username":"admin"}' $baseURL/api/v1/users -sS
echo ""
curl -H "x-token: $apiKey" -H 'Content-Type: application/json' --data '{"id":"extra-user","username":"extra"}' $baseURL/api/v1/users -sS
echo ""

echo "+Creating chore types"
curl -H "x-token: $apiKey" -H 'Content-Type: application/json' --data '{"description":"Laundry","id":"laundry","name":"Laundry"}' $baseURL/api/v1/chore-types -sS
echo ""
curl -H "x-token: $apiKey" -H 'Content-Type: application/json' --data '{"description":"Laundry","id":"gardening","name":"Gardening"}' $baseURL/api/v1/chore-types -sS
echo ""

echo "+Creating weekly chores"
curl -X POST -H "x-token: $apiKey" $baseURL/api/v1/weekly-chores/current -sS
echo ""
curl -X POST -H "x-token: $apiKey" $baseURL/api/v1/weekly-chores/next -sS
echo ""

echo "+Data provisioned"
