package repositories.users;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import models.User;
import repositories.base.BaseAdminRepository;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class UsersRepositoryNonCached extends BaseAdminRepository implements UsersRepository {
  @Inject
  public UsersRepositoryNonCached(Config config, Executor executor) {
    super(config, executor);
  }

  @Override
  public CompletableFuture<List<User>> listUsers() {
    return sendGetRequestAdmin("/api/v1/users", User[].class)
      .thenApply(Arrays::asList);
  }
}
