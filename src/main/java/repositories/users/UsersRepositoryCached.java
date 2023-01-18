package repositories.users;

import com.google.inject.Inject;
import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import models.User;
import repositories.base.BaseRepository;
import services.RedisService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static constants.CacheConstants.USERS_CACHE_EXPIRE_SECONDS;
import static constants.CacheConstants.USERS_REDIS_KEY_PREFIX;

@Slf4j
public class UsersRepositoryCached extends BaseRepository implements UsersRepository {
  private final RedisService redisService;
  private final UsersRepositoryNonCached usersRepository;

  @Inject
  public UsersRepositoryCached(ConfigRepository config, Executor executor, RedisService redisService,
                               UsersRepositoryNonCached usersRepository) {
    super(config, executor);
    this.redisService = redisService;
    this.usersRepository = usersRepository;
  }

  @Override
  public CompletableFuture<List<User>> listUsers() {
    var result = redisService.get(USERS_REDIS_KEY_PREFIX);
    if (result != null) {
      log.debug("Cache hit for users");
      return CompletableFuture.completedFuture(fromJson(result, User[].class))
        .thenApply(List::of);
    }
    log.debug("Cache miss for users");
    return usersRepository.listUsers()
      .thenApply(users -> {
        redisService.setex(USERS_REDIS_KEY_PREFIX, USERS_CACHE_EXPIRE_SECONDS, toJson(users));
        return users;
      });
  }
}
