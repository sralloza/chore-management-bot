package services;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import models.QueryType;

import java.util.Optional;

@Slf4j
public class MessagesService {
  private final RedisService redisService;

  @Inject
  public MessagesService(RedisService redisService) {
    this.redisService = redisService;
  }

  public Optional<Integer> getMessageId(String chatId, QueryType type) {
    if (type == null) {
      return Optional.empty();
    }
    var redisResult = redisService.get(getKey(chatId, type));
    if (redisResult == null) {
      return Optional.empty();
    }
    return Optional.of(Integer.parseInt(redisResult));
  }

  public void saveMessageId(Long chatId, QueryType type, Integer messageId) {
    String key = getKey(chatId, type);
    log.debug("Saving message {} to redis with key {}", messageId, key);
    // Messages in telegram can only be deleted for 48 hours
    redisService.setex(key, 48 * 3600, messageId.toString());
  }

  public void deleteMessageId(String chatId, QueryType type) {
    redisService.del(getKey(chatId, type));
  }

  private static String getKey(Long chatId, QueryType type) {
    return getKey(chatId.toString(), type);
  }

  private static String getKey(String chatId, QueryType type) {
    return "query::" + type.getName() + "::" + chatId;
  }
}
