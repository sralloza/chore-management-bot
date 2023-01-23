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
- ***CHORES_CACHE_ENABLED***: Enable or disable the chores cache. It manages the `listWeeklyChores` and the `listChores` endpoints. Defaults to `true`.
- ***CHORE_TYPES_CACHE_ENABLED***: Enable or disable the users cache. If is disabled, for every message sent to the bot a GET request will be sent to the API. Defaults to `true`.
- ***LATEX_CACHE_ENABLED***: Enable the cache for latex generated images. Defaults to `true`.
- ***TICKETS_CACHE_ENABLED***: Enable or disable the tickets cache. Defaults to `true`.
- ***USERS_CACHE_ENABLED***: Enable or disable the users cache. If is disabled, for every message sent to the bot a GET request will be sent to the API. Defaults to `true`.

## Cache

The bot uses a cache to avoid sending too many requests to the API. These are the expiration times for each cache:

- **latex** (weekly chores and tickets images): 2 Weeks. Each image is saved in a different key in cache.
- **[listUsers endpoint](https://sralloza.github.io/chore-management-api/#tag/Users/operation/listUsers)**: 4 weeks
- **[listTickets endpoint](https://sralloza.github.io/chore-management-api/#tag/Tickets/operation/listTickets)**: 4 weeks. Any chore transfer will invalidate this cache.
- **[listChoreTypes endpoint](https://sralloza.github.io/chore-management-api/#tag/Chore-Types/operation/listChoreTypes)**: 4 weeks.
- **[listChores endpoint](https://sralloza.github.io/chore-management-api/#tag/Chores/operation/listChores)**: 1 week. Any chore transfer or chore completion will invalidate this cache.
- **[listWeeklyChores endpoint](https://sralloza.github.io/chore-management-api/#tag/Weekly-Chores/operation/listWeeklyChores)**: 1 week. Any chore transfer or chore completion will invalidate this cache.
