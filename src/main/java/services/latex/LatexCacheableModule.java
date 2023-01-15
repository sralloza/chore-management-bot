package services.latex;

import services.CacheableModule;

public class LatexCacheableModule extends CacheableModule {
  @Override
  protected void configure() {
    bind(LatexService.class).to(getServiceByConfig(
      "latex",
      LatexServiceCached.class,
      LatexServiceNonCached.class));
  }
}
