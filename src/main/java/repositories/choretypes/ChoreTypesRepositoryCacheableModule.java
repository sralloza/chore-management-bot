package repositories.choretypes;

import base.CacheableModule;

public class ChoreTypesRepositoryCacheableModule extends CacheableModule {
  @Override
  protected void configure() {
    bind(ChoreTypesRepository.class).to(getComponentByConfig(
      "choreTypes",
      ChoreTypesRepositoryCached.class,
      ChoreTypesRepositoryNonCached.class));
  }
}
