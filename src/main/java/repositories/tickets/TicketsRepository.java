package repositories.tickets;

import models.Ticket;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TicketsRepository {
  CompletableFuture<List<Ticket>> listTickets(String userId);
}
