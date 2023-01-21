package repositories.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import exceptions.APIException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.annotation.Nullable;
import java.io.IOException;
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
  protected final Executor executor;
  protected final ObjectMapper objectMapper;
  private final MediaType mediaType = MediaType.get("application/json");
  private final OkHttpClient httpClient;

  public BaseRepository(Config config, Executor executor) {
    this.baseURL = config.getString("api.baseURL");
    this.executor = executor;
    this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    this.httpClient = new OkHttpClient();
  }

  protected <T> CompletableFuture<T> sendRequest(String method, String path, Class<T> clazz,
                                                 @Nullable String token, @Nullable String payload) {

    RequestBody body = payload != null ? RequestBody.create(payload, mediaType) : RequestBody.create("", null);
    Request.Builder requestBuilder = new Request.Builder()
      .url(baseURL + path)
      .header("User-Agent", "ChoreManagement/1.0")
      .header("Accept-Language", "es");

    if (Objects.nonNull(token)) {
      requestBuilder = requestBuilder.header("x-token", token);
    }

    switch (method) {
      case "POST":
        requestBuilder = requestBuilder.post(body);
        break;
      case "PUT":
        requestBuilder = requestBuilder.put(body);
        break;
      case "DELETE":
        requestBuilder = requestBuilder.delete();
        break;
      default:
        requestBuilder = requestBuilder.get();
        break;
    }

    Request request = requestBuilder.build();
    log.debug("Request URL: " + request.url());
    log.debug("Request headers: " + String.join(", ", request.headers().toString().split("\n")));

    OkHttpResponseFuture callback = new OkHttpResponseFuture();
    httpClient.newCall(request).enqueue(callback);
    return callback.future.thenApply(response -> getAndProcessBody(response, clazz));
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

  private Optional<String> processBody(@Nullable ResponseBody body) {
    if (body == null) {
      return Optional.empty();
    }
    try {
      return Optional.of(body.string());
    } catch (IOException e) {
      log.error("Error processing body", e);
      throw new RuntimeException(e);
    }
  }

  private <T> T getAndProcessBody(Response response, Class<T> clazz) {
    log.debug("Response code: " + response.code());
    log.debug("Response headers: " + String.join(", ", response.headers().toString().split("\n")));

    Optional<String> bodyString = processBody(response.body());
    log.debug("Response body: " + bodyString.orElse(null));

    if (!VALID_STATUS_CODES.contains(response.code())) {
      var exception = APIException.from(response, bodyString.orElse(null));
      log.error("Error calling API", exception);
      throw exception;
    }

    if (bodyString.isEmpty()) {
      return null;
    }

    try {
      return fromJson(bodyString.get(), clazz);
    } catch (Exception e) {
      throw APIException.from(response, bodyString.orElse(null), e);
    }
  }
}
