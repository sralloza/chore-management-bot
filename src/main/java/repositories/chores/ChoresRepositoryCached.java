package repositories.chores;

import com.google.inject.Inject;
import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import models.Chore;
import models.WeeklyChores;
import repositories.base.BaseRepositoryCached;
import services.RedisService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static constants.CacheConstants.CHORES_CACHE_EXPIRE_SECONDS;
import static constants.CacheConstants.CHORES_REDIS_KEY_PREFIX;
import static constants.CacheConstants.WEEKLY_CHORES_CACHE_EXPIRE_SECONDS;
import static constants.CacheConstants.WEEKLY_CHORES_REDIS_KEY_PREFIX;

@Slf4j
public class ChoresRepositoryCached extends BaseRepositoryCached implements ChoresRepository {
  private final RedisService redisService;
  private final ChoresRepositoryNonCached choresRepository;

  @Inject
  public ChoresRepositoryCached(ConfigRepository config, Executor executor, RedisService redisService,
                                ChoresRepositoryNonCached choresRepository) {
    super(config, executor, redisService, "chores");
    this.redisService = redisService;
    this.choresRepository = choresRepository;
  }

  @Override
  public CompletableFuture<List<Chore>> listChores(String userId) {
    return getFromCacheList(() -> choresRepository.listChores(userId), Chore[].class, CHORES_CACHE_EXPIRE_SECONDS);
  }

  @Override
  public CompletableFuture<List<WeeklyChores>> listWeeklyChores(String userId) {
    return getFromCacheList(() -> choresRepository.listWeeklyChores(userId), WeeklyChores[].class,
      WEEKLY_CHORES_CACHE_EXPIRE_SECONDS, "weeklyChores");
  }

  @Override
  public CompletableFuture<WeeklyChores> createWeeklyChores(String weekId) {
    return choresRepository.createWeeklyChores(weekId)
      .thenApply(weeklyChores -> {
        deleteChoresCache();
        return weeklyChores;
      });
  }

  @Override
  public CompletableFuture<Void> completeChore(String userId, String weekId, String choreType) {
    return choresRepository.completeChore(userId, weekId, choreType)
      .thenApply(weeklyChores -> {
        deleteChoresCache();
        return weeklyChores;
      });
  }

  private void deleteChoresCache() {
    redisService.del(CHORES_REDIS_KEY_PREFIX);
    redisService.del(WEEKLY_CHORES_REDIS_KEY_PREFIX);
  }
}
