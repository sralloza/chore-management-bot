import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class TestRepositoryBase {
  protected MockWebServer server;

  protected Config config = ConfigFactory.load();

  @BeforeEach
  public void setupBase() throws Exception {
    server = new MockWebServer();
    server.start();
  }

  @AfterEach
  public void tearDown() throws Exception {
    if (Objects.nonNull(server)) {
      server.shutdown();
    }
  }

  protected void setConfig(Map<String, String> config) {
    config.put("api.baseURL", getServerUrl());
    config.put("api.adminApiKey", "adminApiKey");
    config.put("telegram.bot.token", "telegramBotToken");
    config.put("telegram.bot.username", "telegramBotUsername");
    this.config = ConfigFactory.parseMap(config)
      .withFallback(ConfigFactory.load());
  }

  protected String getServerUrl() {
    var url = server.url("").toString();
    url = url.substring(0, url.length() - 1);
    return url;
  }

  protected void setServerRoutes(Map<String, MockResponse> responses) {
    final Dispatcher dispatcher = new Dispatcher() {

      @NotNull
      @Override
      public MockResponse dispatch(RecordedRequest request) {
        var response = responses.get(request.getPath());
        return Optional.ofNullable(response).orElse(
          new MockResponse().setResponseCode(404).setBody("{\"detail\": \"Not Found\"}"));
      }
    };
    server.setDispatcher(dispatcher);
  }

  protected MockResponse mockResponse(int code, @Nullable String body) {
    var response = new MockResponse().setResponseCode(code);
    if (Objects.nonNull(body)) {
      response.setBody(body);
      response.addHeader("Content-Type", "application/json");
    }
    return response;
  }

  protected <T> T getGuiceInstance(Class<T> targetClass) {
    Injector injector = Guice.createInjector(new MainModule());
    return injector.getInstance(targetClass);
  }
}
