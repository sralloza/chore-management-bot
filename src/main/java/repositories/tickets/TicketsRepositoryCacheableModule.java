package repositories.tickets;

import base.CacheableModule;

public class TicketsRepositoryCacheableModule extends CacheableModule {
  @Override
  protected void configure() {
    bind(TicketsRepository.class).to(getComponentByConfig(
      "tickets",
      TicketsRepositoryCached.class,
      TicketsRepositoryNonCached.class));
  }
}
