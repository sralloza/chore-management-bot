package repositories.users;

import base.CacheableModule;

public class UsersRepositoryCacheableModule extends CacheableModule {
  @Override
  protected void configure() {
    bind(UsersRepository.class).to(getComponentByConfig(
      "users",
      UsersRepositoryCached.class,
      UsersRepositoryNonCached.class));
  }
}
