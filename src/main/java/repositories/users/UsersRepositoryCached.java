package repositories.users;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import models.User;
import repositories.base.BaseRepositoryCached;
import services.RedisService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static constants.CacheConstants.USERS_CACHE_EXPIRE_SECONDS;

@Slf4j
public class UsersRepositoryCached extends BaseRepositoryCached implements UsersRepository {
  private final UsersRepositoryNonCached usersRepository;

  @Inject
  public UsersRepositoryCached(Config config, Executor executor, RedisService redisService,
                               UsersRepositoryNonCached usersRepository) {
    super(config, executor, redisService, "users");
    this.usersRepository = usersRepository;
  }

  @Override
  public CompletableFuture<List<User>> listUsers() {
    return getFromCacheList(usersRepository::listUsers, User[].class, USERS_CACHE_EXPIRE_SECONDS);
  }
}
