package repositories;

import models.Ticket;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ChoreManagementRepository {
  CompletableFuture<List<Ticket>> getTickets(String userId);
  CompletableFuture<Void> skipWeek(String userId, String weekId);
  CompletableFuture<Void> unskipWeek(String userId, String weekId);
}
