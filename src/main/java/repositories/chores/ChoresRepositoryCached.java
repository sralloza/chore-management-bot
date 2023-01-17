package repositories.chores;

import com.google.inject.Inject;
import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import models.Chore;
import models.WeeklyChores;
import repositories.BaseRepository;
import security.Security;
import services.RedisService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static constants.CacheConstants.CHORES_CACHE_EXPIRE_SECONDS;
import static constants.CacheConstants.CHORES_REDIS_KEY_PREFIX;
import static constants.CacheConstants.WEEKLY_CHORES_CACHE_EXPIRE_SECONDS;
import static constants.CacheConstants.WEEKLY_CHORES_REDIS_KEY_PREFIX;

@Slf4j
public class ChoresRepositoryCached extends BaseRepository implements ChoresRepository {
  private final RedisService redisService;
  private final ChoresRepositoryNonCached choresRepository;

  @Inject
  public ChoresRepositoryCached(ConfigRepository config, Security security, Executor executor,
                                RedisService redisService, ChoresRepositoryNonCached choresRepository) {
    super(config, security, executor);
    this.redisService = redisService;
    this.choresRepository = choresRepository;
  }

  @Override
  public CompletableFuture<List<Chore>> listChores(String userId) {
    var result = redisService.get(CHORES_REDIS_KEY_PREFIX);
    if (result != null) {
      log.debug("Cache hit for chores");
      return CompletableFuture.completedFuture(fromJson(result, Chore[].class))
        .thenApply(List::of);
    }
    log.debug("Cache miss for chores");
    return choresRepository.listChores(userId)
      .thenApply(chores -> {
        redisService.setex(CHORES_REDIS_KEY_PREFIX, CHORES_CACHE_EXPIRE_SECONDS, toJson(chores));
        return chores;
      });
  }

  @Override
  public CompletableFuture<List<WeeklyChores>> listWeeklyChores(String userId) {
    var result = redisService.get(WEEKLY_CHORES_REDIS_KEY_PREFIX);
    if (result != null) {
      log.debug("Cache hit for weeklyChores");
      return CompletableFuture.completedFuture(fromJson(result, WeeklyChores[].class))
        .thenApply(List::of);
    }
    log.debug("Cache miss for weeklyChores");
    return choresRepository.listWeeklyChores(userId)
      .thenApply(weeklyChores -> {
        redisService.setex(WEEKLY_CHORES_REDIS_KEY_PREFIX, WEEKLY_CHORES_CACHE_EXPIRE_SECONDS, toJson(weeklyChores));
        return weeklyChores;
      });
  }

  @Override
  public CompletableFuture<WeeklyChores> createWeeklyChores(String weekId) {
    return choresRepository.createWeeklyChores(weekId)
      .thenApply(weeklyChores -> {
        redisService.del(CHORES_REDIS_KEY_PREFIX);
        redisService.del(WEEKLY_CHORES_REDIS_KEY_PREFIX);
        return weeklyChores;
      });
  }

  @Override
  public CompletableFuture<Void> completeChore(String userId, String weekId, String choreType) {
    return choresRepository.completeChore(userId, weekId, choreType)
      .thenApply(weeklyChores -> {
        redisService.del(CHORES_REDIS_KEY_PREFIX);
        redisService.del(WEEKLY_CHORES_REDIS_KEY_PREFIX);
        return weeklyChores;
      });
  }
}