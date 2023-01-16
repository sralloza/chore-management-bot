package services;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import models.Chore;
import models.ChoreType;
import models.Ticket;
import models.User;
import models.WeeklyChores;
import repositories.ChoreManagementRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Slf4j
public class ChoreManagementServiceImp implements ChoreManagementService {
  private final ChoreManagementRepository repository;

  @Inject
  public ChoreManagementServiceImp(ChoreManagementRepository repository) {
    this.repository = repository;
  }

  public CompletableFuture<List<Ticket>> getTickets(String userId) {
    return repository.getTickets(userId);
  }

  public CompletableFuture<List<WeeklyChores>> getWeeklyChores(String userId) {
    return repository.getWeeklyChores(userId);
  }

  public CompletableFuture<List<Chore>> getChores(String userId) {
    return repository.getChores(userId);
  }

  public CompletableFuture<Void> completeTask(String userId, String weekId, String choreType) {
    return repository.completeTask(userId, weekId, choreType);
  }

  public CompletableFuture<Void> skipWeek(String userId, String weekId) {
    return repository.skipWeek(userId, weekId);
  }

  public CompletableFuture<Void> unskipWeek(String userId, String weekId) {
    return repository.unskipWeek(userId, weekId);
  }

  @Override
  public CompletableFuture<List<ChoreType>> getChoreTypes() {
    return repository.getChoreTypes();
  }

  public CompletableFuture<WeeklyChores> createWeeklyChores(String weekId) {
    return repository.createWeeklyChores(weekId);
  }

  public CompletableFuture<List<User>> listUsersAdminToken() {
    return repository.listUsersAdminToken();
  }
}
