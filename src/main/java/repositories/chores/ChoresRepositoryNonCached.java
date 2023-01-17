package repositories.chores;

import com.google.inject.Inject;
import config.ConfigRepository;
import models.Chore;
import models.WeeklyChores;
import repositories.BaseRepository;
import security.Security;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ChoresRepositoryNonCached extends BaseRepository implements ChoresRepository {
  @Inject
  public ChoresRepositoryNonCached(ConfigRepository config, Security security, Executor executor) {
    super(config, security, executor);
  }

  @Override
  public CompletableFuture<List<Chore>> listChores(String userId) {
    return sendGetRequest("/api/v1/chores?user_id=me&done=false", Chore[].class, userId)
      .thenApply(Arrays::asList);
  }

  @Override
  public CompletableFuture<List<WeeklyChores>> listWeeklyChores(String userId) {
    return sendGetRequest("/api/v1/weekly-chores?missing_only=true", WeeklyChores[].class, userId)
      .thenApply(Arrays::asList);
  }

  @Override
  public CompletableFuture<WeeklyChores> createWeeklyChores(String weekId) {
    return sendPostRequestAdmin("/api/v1/weekly-chores/" + weekId, WeeklyChores.class);
  }

  @Override
  public CompletableFuture<Void> completeChore(String userId, String weekId, String choreType) {
    String path = "/api/v1/chores/" + weekId + "/type/" + choreType + "/complete";
    return sendPostRequest(path, null, userId);
  }
}