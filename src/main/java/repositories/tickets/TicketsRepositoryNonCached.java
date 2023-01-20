package repositories.tickets;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import models.Ticket;
import repositories.base.BaseNonAdminRepository;
import security.Security;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TicketsRepositoryNonCached extends BaseNonAdminRepository implements TicketsRepository {
  @Inject
  public TicketsRepositoryNonCached(Config config, Security security, Executor executor) {
    super(config, security, executor);
  }

  @Override
  public CompletableFuture<List<Ticket>> listTickets(String userId) {
    return sendGetRequest("/api/v1/tickets", Ticket[].class, userId)
      .thenApply(Arrays::asList);
  }
}
