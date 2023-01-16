package repositories.chores;

import models.Chore;
import models.WeeklyChores;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ChoresRepository {
  CompletableFuture<List<Chore>> listChores(String userId);
  CompletableFuture<List<WeeklyChores>> listWeeklyChores(String userId);
  CompletableFuture<WeeklyChores> createWeeklyChores(String weekId);
  CompletableFuture<Void> completeChore(String userId, String weekId, String choreType);
}
