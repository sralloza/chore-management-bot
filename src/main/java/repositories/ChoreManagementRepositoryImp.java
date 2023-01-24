package repositories;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import models.WeekId;
import repositories.base.BaseNonAdminRepository;
import security.Security;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ChoreManagementRepositoryImp extends BaseNonAdminRepository implements ChoreManagementRepository {
  @Inject
  public ChoreManagementRepositoryImp(Config config, Security security, Executor executor) {
    super(config, security, executor);
  }

  public CompletableFuture<WeekId> skipWeek(String userId, String weekId) {
    return sendPostRequest("/api/v1/users/me/deactivate/" + weekId, WeekId.class, userId);
  }

  public CompletableFuture<WeekId> unSkipWeek(String userId, String weekId) {
    return sendPostRequest("/api/v1/users/me/reactivate/" + weekId, WeekId.class, userId);
  }
}
