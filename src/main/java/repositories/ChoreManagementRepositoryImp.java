package repositories;

import com.google.inject.Inject;
import config.ConfigRepository;
import models.Chore;
import models.ChoreType;
import models.Ticket;
import models.User;
import models.WeeklyChores;
import security.Security;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ChoreManagementRepositoryImp extends BaseRepository implements ChoreManagementRepository {
  @Inject
  public ChoreManagementRepositoryImp(ConfigRepository config, Security security, Executor executor) {
    super(config.getString("api.baseURL"), config.getString("api.adminApiKey"), config, security, executor);
  }

  public CompletableFuture<List<Ticket>> getTickets(String userId) {
    return sendGetRequest("/api/v1/tickets", Ticket[].class, userId)
      .thenApply(Arrays::asList);
  }

  public CompletableFuture<List<WeeklyChores>> getWeeklyChores(String userId) {
    return sendGetRequest("/api/v1/weekly-chores?missing_only=true", WeeklyChores[].class, userId)
      .thenApply(Arrays::asList);
  }

  public CompletableFuture<Void> completeTask(String userId, String weekId, String choreType) {
    String path = "/api/v1/chores/" + weekId + "/type/" + choreType + "/complete";
    return sendPostRequest(path, null, userId);
  }

  public CompletableFuture<List<Chore>> getChores(String userId) {
    return sendGetRequest("/api/v1/chores?user_id=me&done=false", Chore[].class, userId)
      .thenApply(Arrays::asList);
  }

  public CompletableFuture<Void> skipWeek(String userId, String weekId) {
    return sendPostRequest("/api/v1/users/me/deactivate/" + weekId, null, userId);
  }

  public CompletableFuture<Void> unskipWeek(String userId, String weekId) {
    return sendPostRequest("/api/v1/users/me/reactivate/" + weekId, null, userId);
  }

  public CompletableFuture<WeeklyChores> createWeeklyChores(String weekId) {
    return sendPostRequestAdmin("/api/v1/weekly-chores/" + weekId, WeeklyChores.class);
  }

  public CompletableFuture<List<User>> listUsersAdminToken() {
    return sendGetRequestAdmin("/api/v1/users", User[].class)
      .thenApply(Arrays::asList);
  }

  @Override
  public CompletableFuture<List<ChoreType>> getChoreTypes() {
    return sendGetRequestAdmin("/api/v1/chore-types", ChoreType[].class)
      .thenApply(Arrays::asList);
  }
}
