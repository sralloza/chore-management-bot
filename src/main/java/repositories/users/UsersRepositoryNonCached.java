package repositories.users;

import com.google.inject.Inject;
import config.ConfigRepository;
import models.User;
import repositories.BaseRepository;
import security.Security;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class UsersRepositoryNonCached extends BaseRepository implements UsersRepository {
  @Inject
  public UsersRepositoryNonCached(ConfigRepository config, Security security, Executor executor) {
    super(config, security, executor);
  }

  @Override
  public CompletableFuture<List<User>> listUsers() {
      return sendGetRequestAdmin("/api/v1/users", User[].class)
        .thenApply(Arrays::asList);
  }
}
