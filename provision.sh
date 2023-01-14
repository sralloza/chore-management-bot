#!/usr/bin/env bash

EXTRA="x-token:87139ea8-828c-47f7-b45b-f97604138ecf --ignore-stdin"

userId=$(cat .env | grep TELEGRAM_USER_ID | cut -d '=' -f2)
http localhost:8080/api/v1/users username=admin id=$userId $EXTRA
http localhost:8080/api/v1/chore-types id=laundry name=Laundry description=Laundry $EXTRA
http post localhost:8080/api/v1/weekly-chores/current $EXTRA
http post localhost:8080/api/v1/weekly-chores/next $EXTRA
