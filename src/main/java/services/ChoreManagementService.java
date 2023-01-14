package services;

import models.Chore;
import models.ChoreType;
import models.Ticket;
import models.User;
import models.WeeklyChores;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface ChoreManagementService {
  CompletableFuture<List<Ticket>> getTickets(String userId);

  CompletableFuture<List<WeeklyChores>> getWeeklyChores(String userId);

  CompletableFuture<List<Chore>> getChores(String userId);

  CompletableFuture<Void> completeTask(String userId, String weekId, String choreType);

  CompletableFuture<Void> skipWeek(String userId, String weekId);

  CompletableFuture<Void> unskipWeek(String userId, String weekId);

  CompletableFuture<List<ChoreType>> getChoreTypes();

  CompletableFuture<WeeklyChores> createWeeklyChores(String userId, String weekId);

  CompletableFuture<List<User>> listUsersAdminToken();
}
