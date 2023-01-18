package repositories;

import java.util.concurrent.CompletableFuture;

public interface ChoreManagementRepository {
  CompletableFuture<Void> skipWeek(String userId, String weekId);

  CompletableFuture<Void> unSkipWeek(String userId, String weekId);
}
