package repositories.base;

import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import models.User;
import services.RedisService;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Slf4j
public class BaseRepositoryCached extends BaseRepository {
  private final RedisService redisService;
  private final String modelName;

  public BaseRepositoryCached(ConfigRepository config, Executor executor, RedisService redisService, String modelName) {
    super(config, executor);
    this.redisService = redisService;
    this.modelName = modelName;
  }

  private String getCacheKey(String modelName) {
    return "api::" + modelName;
  }

  protected <T> CompletableFuture<List<T>> getFromCacheList(Callable<CompletableFuture<List<T>>> nonCachedCallable,
                                                            Class<T[]> targetClass, int cacheExpireSeconds) {
    return getFromCacheList(nonCachedCallable, targetClass, cacheExpireSeconds, modelName);
  }

  protected <T> CompletableFuture<List<T>> getFromCacheList(Callable<CompletableFuture<List<T>>> nonCachedCallable,
                                                            Class<T[]> targetClass, int cacheExpireSeconds, String modelName) {
    String cacheKey = getCacheKey(modelName);
    var result = redisService.get(cacheKey);
    if (result != null) {
      log.debug("Cache hit for " + modelName + " (key: " + cacheKey + ")");
      log.debug("Cache contents: " + result);
      return CompletableFuture.completedFuture(fromJson(result, targetClass))
        .thenApply(List::of);
    }
    log.debug("Cache miss for " + modelName + " (key: " + cacheKey + ")");
    return execute(nonCachedCallable)
      .thenApply(model -> {
        redisService.setex(cacheKey, cacheExpireSeconds, toJson(model));
        return model;
      });
  }

  private <T> CompletableFuture<T> execute(Callable<CompletableFuture<T>> alternative) {
    try {
      return alternative.call();
    } catch (CompletionException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Error unwrapping future from callable", e);
    }
  }
}
