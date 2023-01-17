package repositories.tickets;

import com.google.inject.Inject;
import config.ConfigRepository;
import models.Ticket;
import repositories.BaseRepository;
import security.Security;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TicketsRepositoryNonCached extends BaseRepository implements TicketsRepository {
  @Inject
  public TicketsRepositoryNonCached(ConfigRepository config, Security security, Executor executor) {
    super(config, security, executor);
  }

  @Override
  public CompletableFuture<List<Ticket>> listTickets(String userId) {
    return sendGetRequest("/api/v1/tickets", Ticket[].class, userId)
      .thenApply(Arrays::asList);
  }
}