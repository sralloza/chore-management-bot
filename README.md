# Chore Management Bot

Telegram bot which uses the [Chore Management API](https://github.com/sralloza/chore-management-api).

## Deploy

Docker images are provided in [dockerhub](https://hub.docker.com/r/sralloza/chore-management-bot).

## Configuration

Configuration is done by setting environment variables.

### Required

- ***TELEGRAM_BOT_TOKEN***: telegram bot token.
- ***TELEGRAM_BOT_USERNAME***: telegram bot username.
- ***TELEGRAM_CREATOR_ID***: telegram userID of the bot creator.

### Optional

- ***ADMIN_API_KEY***: API key with admin privileges of the Chore Management API.
- ***API_BASE_URL***: base url where the Chore Management API is deployed.
- ***API_HTTP2***: Enable HTTP/2. Defaults to `true`.
- ***LATEX_CACHE_ENABLED***: Enable the cache for latex generated images. Defaults to `true`.
- ***CHORE_TYPES_CACHE_ENABLED***: Enable or disable the users cache. If is disabled, for every message sent to the bot a GET request will be sent to the API. Defaults to `true`.
- ***CHORES_CACHE_ENABLED***: Enable or disable the chores cache. It manages the `listWeeklyChores` and the `listChores` endpoints. Defaults to `true`.
- ***USERS_CACHE_ENABLED***: Enable or disable the users cache. If is disabled, for every message sent to the bot a GET request will be sent to the API. Defaults to `true`.
