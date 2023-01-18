package repositories.tickets;

import com.google.inject.Inject;
import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import models.Ticket;
import repositories.base.BaseAdminRepository;
import repositories.base.BaseRepositoryCached;
import services.RedisService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static constants.CacheConstants.TICKETS_CACHE_EXPIRE_SECONDS;
import static constants.CacheConstants.TICKETS_REDIS_KEY_PREFIX;

@Slf4j
public class TicketsRepositoryCached extends BaseRepositoryCached implements TicketsRepository {
  private final RedisService redisService;
  private final TicketsRepositoryNonCached ticketsRepository;

  @Inject
  public TicketsRepositoryCached(ConfigRepository config, Executor executor, RedisService redisService,
                                 TicketsRepositoryNonCached usersRepository) {
    super(config, executor, redisService, "tickets");
    this.redisService = redisService;
    this.ticketsRepository = usersRepository;
  }

  @Override
  public CompletableFuture<List<Ticket>> listTickets(String userId) {
    return getFromCacheList(() -> ticketsRepository.listTickets(userId), Ticket[].class, TICKETS_CACHE_EXPIRE_SECONDS);
  }
}
