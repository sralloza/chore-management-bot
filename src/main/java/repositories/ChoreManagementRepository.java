package repositories;

import models.WeekId;

import java.util.concurrent.CompletableFuture;

public interface ChoreManagementRepository {
  CompletableFuture<WeekId> skipWeek(String userId, String weekId);

  CompletableFuture<WeekId> unSkipWeek(String userId, String weekId);
}
