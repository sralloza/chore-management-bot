import exceptions.APIException;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.users.UsersRepositoryNonCached;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UsersRepositoryTest extends TestRepositoryBase {
  private UsersRepositoryNonCached repository;

  @BeforeEach
  public void setup() {
    setConfig(new HashMap<>());
    repository = new UsersRepositoryNonCached(config, getGuiceInstance(Executor.class));
  }

  @Test
  public void listUsersEmpty() throws ExecutionException, InterruptedException {
    // Given
    setServerRoutes(Map.of("/api/v1/users", mockResponse(200, "[]")));

    // When
    List<User> result = repository.listUsers().get();

    // Then
    assertEquals(0, result.size());
  }

  @Test
  public void listUsersNotEmpty() throws ExecutionException, InterruptedException {
    // Given
    setServerRoutes(Map.of("/api/v1/users", mockResponse(200,
      "[{\"username\": \"username\", \"id\": \"user-id\", \"api_key\": \"api-key\"}]")));

    // When
    List<User> result = repository.listUsers().get();

    // Then
    var expected = List.of(new User().setUsername("username").setId("user-id").setApiKey("api-key"));
    assertEquals(expected, result);
  }

  @Test
  public void server403() {
    // Given
    setServerRoutes(Map.of("/api/v1/users", mockResponse(403, "{\"detail\": \"Admin access required\"}")));

    // When
    CompletableFuture<?> result = repository.listUsers();

    // Then
    ExecutionException exception = assertThrows(ExecutionException.class, result::get);
    assertTrue(exception.getCause() instanceof APIException);
    APIException cause = (APIException) exception.getCause();
    assertEquals("Admin access required", cause.getMsg());
  }

  @Test
  public void serverInvalidData() {
    // Given
    setServerRoutes(Map.of("/api/v1/users", mockResponse(200, "xxxxxx")));

    // When
    CompletableFuture<?> result = repository.listUsers();

    // Then
    ExecutionException exception = assertThrows(ExecutionException.class, result::get);
    assertTrue(exception.getCause() instanceof APIException);
    APIException cause = (APIException) exception.getCause();
    assertNull(cause.getMsg());
    assertTrue(cause.getMessage().contains("Unrecognized token 'xxxxxx'"));
  }
}
