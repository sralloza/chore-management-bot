package repositories.choretypes;

import com.google.inject.Inject;
import config.ConfigRepository;
import models.ChoreType;
import repositories.BaseRepository;
import security.Security;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ChoreTypesRepositoryNonCached extends BaseRepository implements ChoreTypesRepository {
  @Inject
  public ChoreTypesRepositoryNonCached(String baseURL, String apiToken, ConfigRepository config,
                                       Security security, Executor executor) {
    super(config, security, executor);
  }

  @Override
  public CompletableFuture<List<ChoreType>> listChoreTypes() {
      return sendGetRequestAdmin("/api/v1/chore-types", ChoreType[].class)
        .thenApply(Arrays::asList);
  }
}
