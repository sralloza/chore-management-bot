import exceptions.APIException;
import models.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repositories.tickets.TicketsRepositoryNonCached;
import security.Security;

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

public class TicketsRepositoryTest extends TestRepositoryBase {
  private static final String USER_ID = "userId";
  private static final String INVALID_USER_ID = "invalidUserId";
  private TicketsRepositoryNonCached repository;

  @Mock
  private Security security;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    setConfig(new HashMap<>());
    repository = new TicketsRepositoryNonCached(config, security, getGuiceInstance(Executor.class));
    when(security.getUserApiKey(USER_ID)).thenReturn(CompletableFuture.completedFuture("apiKey"));
    when(security.getUserApiKey(INVALID_USER_ID)).thenReturn(CompletableFuture.supplyAsync(() -> {
      throw new RuntimeException("User not found");
    }));
  }

  @Test
  public void listTicketsEmpty() throws ExecutionException, InterruptedException {
    // Given
    setServerRoutes(Map.of("/api/v1/tickets", mockResponse(200, "[]")));

    // When
    List<Ticket> result = repository.listTickets(USER_ID).get();

    // Then
    assertEquals(0, result.size());
  }

  @Test
  public void listTicketsNotEmpty() throws ExecutionException, InterruptedException {
    // Given
    setServerRoutes(Map.of("/api/v1/tickets", mockResponse(200,
      "[{\"description\": \"description\",\"id\": \"id\",\"name\":\"name\",\"tickets_by_user_id\":{\"user-a\":0," +
        "\"user-b\":0},\"tickets_by_user_name\":{\"username-a\":0,\"username-b\":0}}]")));

    // When
    List<Ticket> result = repository.listTickets(USER_ID).get();

    // Then
    var expected = List.of(new Ticket()
      .setDescription("description")
      .setId("id")
      .setName("name")
      .setTicketsByUserId(Map.of("user-a", 0, "user-b", 0))
      .setTicketsByUserName(Map.of("username-a", 0, "username-b", 0)));
    assertEquals(expected, result);
  }

  @Test
  public void listTicketsUserNotFound() {
    // Given
    setServerRoutes(Map.of("/api/v1/tickets", mockResponse(200, "xxx")));

    // When
    CompletableFuture<?> result = repository.listTickets(INVALID_USER_ID);

    // Then
    ExecutionException exception = assertThrows(ExecutionException.class, result::get);
    assertTrue(exception.getCause() instanceof RuntimeException);
    assertEquals("User not found", exception.getCause().getMessage());
  }

  @Test
  public void server403() {
    // Given
    setServerRoutes(Map.of("/api/v1/tickets", mockResponse(403, "{\"detail\": \"Admin access required\"}")));

    // When
    CompletableFuture<?> result = repository.listTickets(USER_ID);

    // Then
    ExecutionException exception = assertThrows(ExecutionException.class, result::get);
    assertTrue(exception.getCause() instanceof APIException);
    APIException cause = (APIException) exception.getCause();
    assertEquals("Admin access required", cause.getMsg());
  }

  @Test
  public void serverInvalidData() {
    // Given
    setServerRoutes(Map.of("/api/v1/tickets", mockResponse(200, "xxxxxx")));

    // When
    CompletableFuture<?> result = repository.listTickets(USER_ID);

    // Then
    ExecutionException exception = assertThrows(ExecutionException.class, result::get);
    assertTrue(exception.getCause() instanceof APIException);
    APIException cause = (APIException) exception.getCause();
    assertNull(cause.getMsg());
    assertTrue(cause.getMessage().contains("Unrecognized token 'xxxxxx'"));
  }
}
