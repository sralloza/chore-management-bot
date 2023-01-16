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
    super(config, security, executor);
  }

  public CompletableFuture<List<Ticket>> getTickets(String userId) {
    return sendGetRequest("/api/v1/tickets", Ticket[].class, userId)
      .thenApply(Arrays::asList);
  }

  public CompletableFuture<Void> skipWeek(String userId, String weekId) {
    return sendPostRequest("/api/v1/users/me/deactivate/" + weekId, null, userId);
  }

  public CompletableFuture<Void> unskipWeek(String userId, String weekId) {
    return sendPostRequest("/api/v1/users/me/reactivate/" + weekId, null, userId);
  }
}
