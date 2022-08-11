package config;

import com.google.inject.Inject;
import com.typesafe.config.Config;

public class ConfigRepository {
    private final Config config;

    @Inject
    public ConfigRepository(Config config) {
        this.config = config;
    }

    public String getString(String key) {
        return config.getString(key);
    }

    public Boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    public Long getLong(String key) {
        return config.getLong(key);
    }
}
