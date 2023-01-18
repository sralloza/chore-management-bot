package repositories.choretypes;

import com.google.inject.Inject;
import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import models.ChoreType;
import repositories.base.BaseRepository;
import services.RedisService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static constants.CacheConstants.CHORE_TYPES_CACHE_EXPIRE_SECONDS;

@Slf4j
public class ChoreTypesRepositoryCached extends BaseRepository implements ChoreTypesRepository {
  private final RedisService redisService;
  private final ChoreTypesRepositoryNonCached choreTypesRepository;

  @Inject
  public ChoreTypesRepositoryCached(ConfigRepository config, Executor executor, RedisService redisService,
                                    ChoreTypesRepositoryNonCached choreTypesRepository) {
    super(config, executor);
    this.redisService = redisService;
    this.choreTypesRepository = choreTypesRepository;
  }

  @Override
  public CompletableFuture<List<ChoreType>> listChoreTypes() {
    var result = redisService.get("api::choreTypes");
    if (result != null) {
      log.debug("Cache hit for chore types");
      return CompletableFuture.completedFuture(fromJson(result, ChoreType[].class))
        .thenApply(List::of);
    }
    log.debug("Cache miss for chore types");
    return choreTypesRepository.listChoreTypes()
      .thenApply(choreTypes -> {
        redisService.setex("api::choreTypes", CHORE_TYPES_CACHE_EXPIRE_SECONDS, toJson(choreTypes));
        return choreTypes;
      });
  }
}
