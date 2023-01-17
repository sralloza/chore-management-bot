package services.latex;

import java.util.UUID;

public class BaseLatexService {
  public String getFileName(String redisKeyPrefix) {
    return redisKeyPrefix + "." + UUID.randomUUID() + ".png";
  }
}
