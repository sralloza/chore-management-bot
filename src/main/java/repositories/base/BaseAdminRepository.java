package repositories.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Singleton
public class BaseAdminRepository extends BaseRepository {
  private static final Set<Integer> VALID_STATUS_CODES = Set.of(200, 201, 204);
  private final String adminApiKey;
  protected final Executor executor;
  protected final ObjectMapper objectMapper;

  public BaseAdminRepository(Config config, Executor executor) {
    super(config, executor);
    this.adminApiKey = config.getString("api.adminApiKey");
    this.executor = executor;
    this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  }

  protected <T> CompletableFuture<T> sendPostRequestAdmin(String path, Class<T> clazz) {
    return sendRequest("POST", path, clazz, adminApiKey, null);
  }

  protected <T> CompletableFuture<T> sendGetRequestAdmin(String path, Class<T> clazz) {
    return sendRequest("GET", path, clazz, adminApiKey, null);
  }
}
