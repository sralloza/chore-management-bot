package repositories.chores;

import base.CacheableModule;

public class ChoresRepositoryCacheableModule extends CacheableModule {
  @Override
  protected void configure() {
    bind(ChoresRepository.class).to(getComponentByConfig(
      "chores",
      ChoresRepositoryCached.class,
      ChoresRepositoryNonCached.class));
  }
}
