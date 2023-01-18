package constants;

import static repositories.base.BaseRepositoryCached.getCacheKeyFromModelName;

public class CacheConstants {
  public static final int LATEX_CACHE_EXPIRE_SECONDS = 2 * 7 * 24 * 3600;
  public static final int USERS_CACHE_EXPIRE_SECONDS = 4 * 7 * 24 * 3600;
  public static final int TICKETS_CACHE_EXPIRE_SECONDS = 4 * 7 * 24 * 3600;
  public static final int CHORE_TYPES_CACHE_EXPIRE_SECONDS = 4 * 7 * 24 * 3600;
  public static final int CHORES_CACHE_EXPIRE_SECONDS = 7 * 24 * 3600;
  public static final int WEEKLY_CHORES_CACHE_EXPIRE_SECONDS = 7 * 24 * 3600;

  public static final String USERS_REDIS_KEY_PREFIX = getCacheKeyFromModelName("users");
  public static final String CHORES_REDIS_KEY_PREFIX = getCacheKeyFromModelName("chores");
  public static final String WEEKLY_CHORES_REDIS_KEY_PREFIX = getCacheKeyFromModelName("weeklyChores");
  public static final String TICKETS_REDIS_KEY_PREFIX = getCacheKeyFromModelName("tickets");
}
