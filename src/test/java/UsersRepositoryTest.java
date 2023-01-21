import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.users.UsersRepositoryNonCached;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersRepositoryTest extends TestRepositoryBase {
  public static final String USERS_URL = "/api/v1/users";
  private UsersRepositoryNonCached repository;

  @BeforeEach
  public void setup() {
    setConfig(new HashMap<>());
    repository = new UsersRepositoryNonCached(config, getGuiceInstance(Executor.class));
  }

  @Test
  public void listUsersEmpty() throws ExecutionException, InterruptedException {
    // Given
    setServerRoutes(Map.of(USERS_URL, mockResponse(200, "[]")));

    // When
    List<User> result = repository.listUsers().get();

    // Then
    assertEquals(0, result.size());
  }

  @Test
  public void listUsersNotEmpty() throws ExecutionException, InterruptedException {
    // Given
    setServerRoutes(Map.of(USERS_URL, mockResponse(200,
      "[{\"username\": \"username\", \"id\": \"user-id\", \"api_key\": \"api-key\"}]")));

    // When
    List<User> result = repository.listUsers().get();

    // Then
    var expected = List.of(new User().setUsername("username").setId("user-id").setApiKey("api-key"));
    assertEquals(expected, result);
  }

  @Test
  public void listUsers403() {
    testServer403Response(USERS_URL, repository::listUsers);
  }

  @Test
  public void listUsersInvalidData() {
    listServer200UnexpectedData(USERS_URL, repository::listUsers);
  }
}
