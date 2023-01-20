package repositories.choretypes;

import models.ChoreType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ChoreTypesRepository {
  CompletableFuture<List<ChoreType>> listChoreTypes();
}
