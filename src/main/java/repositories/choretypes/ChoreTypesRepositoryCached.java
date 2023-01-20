package repositories.choretypes;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import models.ChoreType;
import repositories.base.BaseRepositoryCached;
import services.RedisService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static constants.CacheConstants.CHORE_TYPES_CACHE_EXPIRE_SECONDS;

@Slf4j
public class ChoreTypesRepositoryCached extends BaseRepositoryCached implements ChoreTypesRepository {
  private final ChoreTypesRepositoryNonCached choreTypesRepository;

  @Inject
  public ChoreTypesRepositoryCached(Config config, Executor executor, RedisService redisService,
                                    ChoreTypesRepositoryNonCached choreTypesRepository) {
    super(config, executor, redisService, "choreTypes");
    this.choreTypesRepository = choreTypesRepository;
  }

  @Override
  public CompletableFuture<List<ChoreType>> listChoreTypes() {
    return getFromCacheList(choreTypesRepository::listChoreTypes, ChoreType[].class,
      CHORE_TYPES_CACHE_EXPIRE_SECONDS);
  }
}
