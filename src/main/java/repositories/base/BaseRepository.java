package repositories.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import exceptions.APIException;
import lombok.extern.slf4j.Slf4j;

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
  private final boolean http2;
  protected final Executor executor;
  protected final ObjectMapper objectMapper;

  public BaseRepository(Config config, Executor executor) {
    this.baseURL = config.getString("api.baseURL");
    this.http2 = config.getBoolean("api.http2");
    this.executor = executor;
    this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  }

  private HttpClient.Version getHttpClientVersion() {
    return http2 ? HttpClient.Version.HTTP_2 : HttpClient.Version.HTTP_1_1;
  }

  protected <T> CompletableFuture<T> sendRequest(String method, String path, Class<T> clazz,
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
      .thenApplyAsync(response -> getAndProcessBody(response, clazz), executor);
  }

  protected <T> T fromJson(String body, Class<T> clazz) {
    if (clazz == null) {
      return null;
    }

    try {
      return objectMapper.readValue(body, clazz);
    } catch (JsonProcessingException e) {
      log.error("Error parsing json: " + body, e);
      throw new RuntimeException(e);
    }
  }

  protected String toJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error("Error creating json: " + object, e);
      throw new RuntimeException(e);
    }
  }

  private <T> T getAndProcessBody(HttpResponse<String> response, Class<T> clazz) {
    log.debug("Response code: " + response.statusCode());
    log.debug("Response headers: " + response.headers());
    log.debug("Response body: " + response.body());

    if (!VALID_STATUS_CODES.contains(response.statusCode())) {
      var exception = new APIException(response);
      log.error("Error calling API", exception);
      throw exception;
    }

    try {
      return fromJson(response.body(), clazz);
    } catch (Exception e) {
      throw new APIException(response, e);
    }
  }
}
