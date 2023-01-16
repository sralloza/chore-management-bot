package repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Singleton;
import config.ConfigRepository;
import exceptions.APIException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import security.Security;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Singleton
public class BaseRepository {
  private static final Set<Integer> VALID_STATUS_CODES = Set.of(200, 201, 204);
  private final String baseURL;
  private final String apiToken;
  private final boolean http2;
  private final Security security;
  protected final Executor executor;

  public BaseRepository(String baseURL, String apiToken, ConfigRepository config,
                        Security security, Executor executor) {
    this.baseURL = baseURL;
    this.apiToken = apiToken;
    this.http2 = config.getBoolean("api.http2");
    this.security = security;
    this.executor = executor;
  }

  private HttpClient.Version getHttpClientVersion() {
    return http2 ? HttpClient.Version.HTTP_2 : HttpClient.Version.HTTP_1_1;
  }

  protected <T> CompletableFuture<T> sendGetRequest(String path, Class<T> clazz, String userId) {
    String token = security.getTenantToken(userId);
    return sendRequest("GET", path, clazz, token, null);
  }

  protected <T> CompletableFuture<T> sendPostRequest(String path, Class<T> clazz, String userId) {
    String token = security.getTenantToken(userId);
    return sendRequest("POST", path, clazz, token, null);
  }

  protected <T> CompletableFuture<T> sendPostRequestAdmin(String path, Class<T> clazz) {
    return sendRequest("POST", path, clazz, apiToken, null);
  }

  protected <T> CompletableFuture<T> sendGetRequestAdmin(String path, Class<T> clazz) {
    return sendRequest("GET", path, clazz, apiToken, null);
  }

  private <T> CompletableFuture<T> sendRequest(String method, String path, Class<T> clazz,
                                               @Nullable String token, @Nullable String payload) {
    HttpClient client = HttpClient.newHttpClient();
    HttpClient.Version version = getHttpClientVersion();
    log.debug("Using {}", version);

    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
      .version(version)
      .uri(URI.create(baseURL + path))
      .header("Content-Type", "application/json")
      .header("Accept-Language", "es")
      .header("User-Agent", "ChoreManagementBot/1.0");

    Map<String, String> headers = new HashMap<>();
    if (Objects.nonNull(token)) {
      headers.put("x-token", token);
    }

    for (Map.Entry<String, String> header : headers.entrySet()) {
      requestBuilder = requestBuilder.header(header.getKey(), header.getValue());
    }

    HttpRequest.BodyPublisher bodyPublisher = Optional.ofNullable(payload)
      .map(HttpRequest.BodyPublishers::ofString)
      .orElse(HttpRequest.BodyPublishers.noBody());

    switch (method) {
      case "POST":
        requestBuilder = requestBuilder.POST(bodyPublisher);
        break;
      case "PUT":
        requestBuilder = requestBuilder.PUT(bodyPublisher);
        break;
      case "DELETE":
        requestBuilder = requestBuilder.DELETE();
        break;
      default:
        requestBuilder = requestBuilder.GET();
        break;
    }

    HttpRequest request = requestBuilder.build();
    log.debug("Request URL: " + request.uri());
    log.debug("Request headers: " + request.headers());

    return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
      .thenApplyAsync(this::getBodyOpt, executor)
      .thenApplyAsync(bodyOpt -> bodyOpt.map(body -> processBody(body, clazz)).orElse(null), executor);
  }

  private <T> T processBody(String body, Class<T> clazz) {
    if (clazz == null) {
      return null;
    }

    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    try {
      return mapper.readValue(body, clazz);
    } catch (JsonProcessingException e) {
      log.error("Error parsing response: " + body, e);
      return null;
    }
  }

  @NotNull
  private Optional<String> getBodyOpt(HttpResponse<String> response) throws APIException {
    log.debug("Response code: " + response.statusCode());
    log.debug("Response headers: " + response.headers());
    log.debug("Response body: " + response.body());

    if (!VALID_STATUS_CODES.contains(response.statusCode())) {
      var exception = new APIException(response);
      log.error("Error calling API", exception);
      throw exception;
    }

    return Optional.of(response.body());
  }
}
