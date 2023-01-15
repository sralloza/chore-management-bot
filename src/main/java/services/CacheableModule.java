package services;

import com.google.inject.AbstractModule;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheableModule extends AbstractModule {
  public <T> Class<? extends T> getServiceByConfig(String tag, Class<? extends T> cachedClass, Class<? extends T> nonCachedClass) {
    boolean result = ConfigFactory.load().getBoolean("cache." + tag + ".enabled");
    log.info("Cache for " + tag + " is " + (result ? "enabled" : "disabled"));
    return result ? cachedClass : nonCachedClass;
  }
}
