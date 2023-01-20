package services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import models.QueryType;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Slf4j
@Singleton
public class RedisService {
    private final Jedis jedis;

    @Inject
    public RedisService(Config config) {
        var host = config.getString("redis.host");
        var port = config.getInt("redis.port");
        this.jedis = new Jedis(host, port);
        if (!isRedisConnected()) {
            throw new RuntimeException("Redis is not connected (" + host + ":" + port + ")");
        }
        log.info("Redis is connected (" + host + ":" + port + ")");
    }

    private boolean isRedisConnected() {
        try {
            jedis.get("test");
            return true;
        } catch (JedisConnectionException e) {
            return false;
        }
    }

    public void setex(String key, int expire, String value) {
        jedis.setex(key, expire, value);
    }

    public void del(String key) {
        log.debug("Deleting key {}", key);
        jedis.del(key);
    }

    public String get(String key) {
        return jedis.get(key);
    }
}
