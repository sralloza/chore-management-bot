import models.ChoreType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.choretypes.ChoreTypesRepositoryNonCached;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChoreTypesRepositoryTest extends TestRepositoryBase {
  private static final String CHORE_TYPES_URL = "/api/v1/chore-types";
  private ChoreTypesRepositoryNonCached repository;

  @BeforeEach
  public void setup() {
    setConfig(new HashMap<>());
    repository = new ChoreTypesRepositoryNonCached(config, getGuiceInstance(Executor.class));
  }

  @Test
  public void listChoreTypesEmpty() throws ExecutionException, InterruptedException {
    // Given
    setServerRoutes(Map.of(CHORE_TYPES_URL, mockResponse(200, "[]")));

    // When
    List<ChoreType> result = repository.listChoreTypes().get();

    // Then
    assertEquals(0, result.size());
  }

  @Test
  public void listChoreTypesNotEmpty() throws ExecutionException, InterruptedException {
    // Given
    setServerRoutes(Map.of(CHORE_TYPES_URL, mockResponse(200,
      "[{\"id\": \"kitchen\", \"name\": \"Kitchen\", \"description\": \"Clean the kitchen\"}]")));

    // When
    List<ChoreType> result = repository.listChoreTypes().get();

    // Then
    var expected = List.of(new ChoreType().setId("kitchen").setName("Kitchen").setDescription("Clean the kitchen"));
    assertEquals(expected, result);
  }

  @Test
  public void listChoreTypes403() {
    testServer403Response(CHORE_TYPES_URL, repository::listChoreTypes);
  }

  @Test
  public void listChoreTypesInvalidResponse() {
    listServer200UnexpectedData(CHORE_TYPES_URL, repository::listChoreTypes);
  }
}
