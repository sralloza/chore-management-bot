package services;

import models.Chore;
import models.ChoreType;
import models.Ticket;
import models.User;
import models.WeekId;
import models.WeeklyChores;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface ChoreManagementService {
  CompletableFuture<List<Ticket>> listTickets(String userId);

  CompletableFuture<List<WeeklyChores>> getWeeklyChores(String userId);

  CompletableFuture<List<Chore>> listChores(String userId);

  CompletableFuture<Void> completeChore(String userId, String weekId, String choreType);

  CompletableFuture<WeekId> skipWeek(String userId, String weekId);

  CompletableFuture<WeekId> unSkipWeek(String userId, String weekId);

  CompletableFuture<List<ChoreType>> listChoreTypes();

  CompletableFuture<WeeklyChores> createWeeklyChores(String weekId);

  CompletableFuture<List<User>> listUsers();
}
