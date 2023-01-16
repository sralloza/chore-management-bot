package services.latex;

import base.CacheableModule;

public class LatexCacheableModule extends CacheableModule {
  @Override
  protected void configure() {
    bind(LatexService.class).to(getComponentByConfig(
      "latex",
      LatexServiceCached.class,
      LatexServiceNonCached.class));
  }
}
