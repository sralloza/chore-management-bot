package repositories.tickets;

import com.google.inject.Inject;
import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import models.Ticket;
import repositories.BaseRepository;
import security.Security;
import services.RedisService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static constants.CacheConstants.TICKETS_CACHE_EXPIRE_SECONDS;
import static constants.CacheConstants.TICKETS_REDIS_KEY_PREFIX;

@Slf4j
public class TicketsRepositoryCached extends BaseRepository implements TicketsRepository {
  private final RedisService redisService;
  private final TicketsRepositoryNonCached ticketsRepository;

  @Inject
  public TicketsRepositoryCached(ConfigRepository config, Security security, Executor executor,
                                 RedisService redisService, TicketsRepositoryNonCached usersRepository) {
    super(config, security, executor);
    this.redisService = redisService;
    this.ticketsRepository = usersRepository;
  }

  @Override
  public CompletableFuture<List<Ticket>> listTickets(String userId) {
    var result = redisService.get(TICKETS_REDIS_KEY_PREFIX);
    if (result != null) {
      log.debug("Cache hit for tickets");
      return CompletableFuture.completedFuture(fromJson(result, Ticket[].class))
        .thenApply(List::of);
    }
    log.debug("Cache miss for tickets");
    return ticketsRepository.listTickets(userId)
      .thenApply(tickets -> {
        redisService.setex(TICKETS_REDIS_KEY_PREFIX, TICKETS_CACHE_EXPIRE_SECONDS, toJson(tickets));
        return tickets;
      });
  }
}
