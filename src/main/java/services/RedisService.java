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

    public void saveMessage(Long chatId, QueryType type, Integer messageId) {
        String key = String.format("%s:%s", chatId, type);
        log.debug("Saving message {} with key {}", messageId, key);
        try {
            jedis.set(key, messageId.toString());
        } catch (JedisConnectionException e) {
            log.error("Redis is not connected", e);
        }
    }

    public Integer getMessage(String chatId, QueryType type) {
        if (type == null) {
            return null;
        }
        
        String key = String.format("%s:%s", chatId, type);
        log.debug("Getting message with key {}", key);
        String result;
        try {
            result = jedis.get(key);
            log.debug("Result: {}", result);
        } catch (JedisConnectionException e) {
            log.error("Redis is not connected", e);
            return null;
        }
        if (result != null) {
            log.debug("Deleting key {}", key);
            jedis.del(key);
            log.debug("Key {} deleted", key);
            return Integer.parseInt(result);
        }
        return null;
    }
}
