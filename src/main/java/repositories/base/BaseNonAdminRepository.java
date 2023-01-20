package repositories.base;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import security.Security;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
public class BaseNonAdminRepository extends BaseAdminRepository {
  private static final Set<Integer> VALID_STATUS_CODES = Set.of(200, 201, 204);
  private final Security security;
  protected final Executor executor;

  public BaseNonAdminRepository(Config config, Security security, Executor executor) {
    super(config, executor);
    this.security = security;
    this.executor = executor;
  }

  protected <T> CompletableFuture<T> sendGetRequest(String path, Class<T> clazz, String userId) {
    return security.getUserApiKey(userId)
      .thenComposeAsync(token -> sendRequest("GET", path, clazz, token, null), executor);
  }

  protected <T> CompletableFuture<T> sendPostRequest(String path, Class<T> clazz, String userId) {
    return security.getUserApiKey(userId)
      .thenComposeAsync(token -> sendRequest("POST", path, clazz, token, null), executor);
  }
}
