package repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class BaseRepository {
    private static final Set<Integer> VALID_STATUS_CODES = Set.of(200, 201, 204);
    private final String baseURL;
    private final String apiToken;
    private final boolean http2;
    private final Security security;

    public BaseRepository(String baseURL, String apiToken,
                          ConfigRepository config, Security security) {
        this.baseURL = baseURL;
        this.apiToken = apiToken;
        this.http2 = config.getBoolean("api.http2");
        this.security = security;
    }

    private HttpClient.Version getHttpClientVersion() {
        return http2 ? HttpClient.Version.HTTP_2 : HttpClient.Version.HTTP_1_1;
    }

    protected <T> CompletableFuture<T> sendGetRequest(String path, Class<T> clazz, Long tenantId) {
        String token = security.getTenantToken(tenantId);
        return sendRequest("GET", path, clazz, token);
    }

    protected <T> CompletableFuture<T> sendPostRequest(String path, Class<T> clazz, Long tenantId) {
        String token = security.getTenantToken(tenantId);
        return sendRequest("POST", path, clazz, token);
    }

    protected <T> CompletableFuture<T> sendGetRequestAdmin(String path, Class<T> clazz) {
        return sendRequest("GET", path, clazz, apiToken);
    }

    private <T> CompletableFuture<T> sendRequest(String method, String path, Class<T> clazz, @Nullable String token) {
        HttpClient client = HttpClient.newHttpClient();
        HttpClient.Version version = getHttpClientVersion();
        log.debug("Using {}", version);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .version(version)
            .uri(URI.create(baseURL + path))
            .header("Content-Type", "application/json")
            .header("User-Agent", "ChoreManagementBot/1.0");

        Map<String, String> headers = new HashMap<>();
        if (token != null) {
            headers.put("x-token", token);
        }

        for (Map.Entry<String, String> header : headers.entrySet()) {
            requestBuilder = requestBuilder.header(header.getKey(), header.getValue());
        }

        switch (method) {
            case "POST":
                requestBuilder = requestBuilder.POST(HttpRequest.BodyPublishers.noBody());
                break;
            case "PUT":
                requestBuilder = requestBuilder.PUT(HttpRequest.BodyPublishers.noBody());
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
            .thenApplyAsync(this::getBodyOpt)
            .thenApplyAsync(bodyOpt -> bodyOpt.map(body -> processBody(body, clazz)).orElse(null));
    }

    private <T> T processBody(String body, Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(body, clazz);
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing response: " + body);
            log.error("Error parsing response: " + body, e);
            e.printStackTrace();
            return null;
        }
    }

    @NotNull
    private Optional<String> getBodyOpt(HttpResponse<String> response) {
        log.debug("Response code: " + response.statusCode());
        log.debug("Response headers: " + response.headers());
        log.debug("Response body: " + response.body());

        if (!VALID_STATUS_CODES.contains(response.statusCode())) {
            throw new APIException(response);
        }

        return Optional.of(response.body());
    }
}
