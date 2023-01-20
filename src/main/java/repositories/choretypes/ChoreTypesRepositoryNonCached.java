package repositories.choretypes;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import models.ChoreType;
import repositories.base.BaseAdminRepository;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ChoreTypesRepositoryNonCached extends BaseAdminRepository implements ChoreTypesRepository {
  @Inject
  public ChoreTypesRepositoryNonCached(Config config, Executor executor) {
    super(config, executor);
  }

  @Override
  public CompletableFuture<List<ChoreType>> listChoreTypes() {
    return sendGetRequestAdmin("/api/v1/chore-types", ChoreType[].class)
      .thenApply(Arrays::asList);
  }
}
