import exceptions.APIException;
import models.Chore;
import models.WeeklyChore;
import models.WeeklyChores;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositories.chores.ChoresRepositoryNonCached;
import security.Security;

import java.time.LocalDateTime;
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
import static org.mockito.Mockito.when;

public class ChoresRepositoryTest extends TestRepositoryBase {
  private static final String USER_ID = "userId";
  private static final String INVALID_USER_ID = "invalidUserId";
  private static final String CHORES_URL = "/api/v1/chores?user_id=me&done=false";
  private static final String WEEKLY_CHORES_URL = "/api/v1/weekly-chores?missing_only=true";
  private ChoresRepositoryNonCached repository;

  @Mock
  private Security security;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    setConfig(new HashMap<>());
    repository = new ChoresRepositoryNonCached(config, security, getGuiceInstance(Executor.class));
    when(security.getUserApiKey(USER_ID)).thenReturn(CompletableFuture.completedFuture("apiKey"));
    when(security.getUserApiKey(INVALID_USER_ID)).thenReturn(CompletableFuture.supplyAsync(() -> {
      throw new RuntimeException("User not found");
    }));
  }

  @Test
  public void listChoresEmpty() throws ExecutionException, InterruptedException {
    // Given
    setServerRoutes(Map.of(CHORES_URL, mockResponse(200, "[]")));

    // When
    List<Chore> result = repository.listChores(USER_ID).get();

    // Then
    assertEquals(0, result.size());
  }

  @Test
  public void listChoresNotEmpty() throws ExecutionException, InterruptedException {
    // Given
    setServerRoutes(Map.of(CHORES_URL, mockResponse(200,
      "[{\"chore_type_id\": \"chore-type-id\",\"completed_at\": null,\"created_at\": \"2023-01-15T22:02:32\"," +
        "\"done\": false,\"user_id\": \"user-id\",\"week_id\": \"2023.03\"}]")));

    // When
    List<Chore> result = repository.listChores(USER_ID).get();

    // Then
    var expected = List.of(new Chore()
      .setChoreTypeId("chore-type-id")
      .setUserId("user-id")
      .setWeekId("2023.03")
      .setDone(false)
      .setCreatedAt(LocalDateTime.of(2023, 1, 15, 22, 2, 32))
      .setCompletedAt(null));
    assertEquals(expected, result);
  }

  @Test
  public void listChoresUserNotFound() {
    // Given
    setServerRoutes(Map.of(CHORES_URL, mockResponse(200, "xxx")));

    // When
    CompletableFuture<?> result = repository.listChores(INVALID_USER_ID);

    // Then
    ExecutionException exception = assertThrows(ExecutionException.class, result::get);
    assertTrue(exception.getCause() instanceof RuntimeException);
    assertEquals("User not found", exception.getCause().getMessage());
  }

  @Test
  public void listChores403() {
    // Given
    setServerRoutes(Map.of(CHORES_URL, mockResponse(403, "{\"detail\": \"Admin access required\"}")));

    // When
    CompletableFuture<?> result = repository.listChores(USER_ID);

    // Then
    ExecutionException exception = assertThrows(ExecutionException.class, result::get);
    assertTrue(exception.getCause() instanceof APIException);
    APIException cause = (APIException) exception.getCause();
    assertEquals("Admin access required", cause.getMsg());
  }

  @Test
  public void listChoresInvalidResponse() {
    // Given
    setServerRoutes(Map.of(CHORES_URL, mockResponse(200, "xxxxxx")));

    // When
    CompletableFuture<?> result = repository.listChores(USER_ID);

    // Then
    ExecutionException exception = assertThrows(ExecutionException.class, result::get);
    assertTrue(exception.getCause() instanceof APIException);
    APIException cause = (APIException) exception.getCause();
    assertNull(cause.getMsg());
    assertTrue(cause.getMessage().contains("Unrecognized token 'xxxxxx'"));
  }

  @Test
  public void listWeeklyChoresEmpty() throws ExecutionException, InterruptedException {
    // Given
    setServerRoutes(Map.of(WEEKLY_CHORES_URL, mockResponse(200, "[]")));

    // When
    List<WeeklyChores> result = repository.listWeeklyChores(USER_ID).get();

    // Then
    assertEquals(0, result.size());
  }

  @Test
  public void listWeeklyChoresNotEmpty() throws ExecutionException, InterruptedException {
    // Given
    setServerRoutes(Map.of(WEEKLY_CHORES_URL, mockResponse(200,
      "[{\"chores\": [{\"assigned_ids\": [\"user-id\"],\"assigned_usernames\": [\"user-name\"],\"chore_type_id\":" +
        "\"chore-type-id\",\"done\": false}],\"week_id\": \"2023.03\"}]")));

    // When
    List<WeeklyChores> result = repository.listWeeklyChores(USER_ID).get();

    // Then
    var expected = List.of(new WeeklyChores()
        .setChores(List.of(new WeeklyChore()
          .setChoreTypeId("chore-type-id")
          .setDone(false)
          .setAssignedIds(List.of("user-id"))
          .setAssignedUsernames(List.of("user-name"))))
        .setWeekId("2023.03"));
    assertEquals(expected, result);
  }

  @Test
  public void listWeeklyChoresUserNotFound() {
    // Given
    setServerRoutes(Map.of(WEEKLY_CHORES_URL, mockResponse(200, "xxx")));

    // When
    CompletableFuture<?> result = repository.listWeeklyChores(INVALID_USER_ID);

    // Then
    ExecutionException exception = assertThrows(ExecutionException.class, result::get);
    assertTrue(exception.getCause() instanceof RuntimeException);
    assertEquals("User not found", exception.getCause().getMessage());
  }

  @Test
  public void listWeeklyChores403() {
    // Given
    setServerRoutes(Map.of(WEEKLY_CHORES_URL, mockResponse(403, "{\"detail\": \"Admin access required\"}")));

    // When
    CompletableFuture<?> result = repository.listWeeklyChores(USER_ID);

    // Then
    ExecutionException exception = assertThrows(ExecutionException.class, result::get);
    assertTrue(exception.getCause() instanceof APIException);
    APIException cause = (APIException) exception.getCause();
    assertEquals("Admin access required", cause.getMsg());
  }

  @Test
  public void listWeeklyChoresInvalidResponse() {
    // Given
    setServerRoutes(Map.of(WEEKLY_CHORES_URL, mockResponse(200, "xxxxxx")));

    // When
    CompletableFuture<?> result = repository.listWeeklyChores(USER_ID);

    // Then
    ExecutionException exception = assertThrows(ExecutionException.class, result::get);
    assertTrue(exception.getCause() instanceof APIException);
    APIException cause = (APIException) exception.getCause();
    assertNull(cause.getMsg());
    assertTrue(cause.getMessage().contains("Unrecognized token 'xxxxxx'"));
  }
}
