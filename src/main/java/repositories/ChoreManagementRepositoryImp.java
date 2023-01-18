package repositories;

import com.google.inject.Inject;
import config.ConfigRepository;
import repositories.base.BaseNonAdminRepository;
import security.Security;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ChoreManagementRepositoryImp extends BaseNonAdminRepository implements ChoreManagementRepository {
  @Inject
  public ChoreManagementRepositoryImp(ConfigRepository config, Security security, Executor executor) {
    super(config, security, executor);
  }

  public CompletableFuture<Void> skipWeek(String userId, String weekId) {
    return sendPostRequest("/api/v1/users/me/deactivate/" + weekId, null, userId);
  }

  public CompletableFuture<Void> unSkipWeek(String userId, String weekId) {
    return sendPostRequest("/api/v1/users/me/reactivate/" + weekId, null, userId);
  }
}
