package utils;

import java.util.Map;
import java.util.function.Function;

public class Internationalization {
  public static Map<String, String> WEEK_ID_KEYWORDS_MAP = Map.of(
    "actual", "current", "anterior", "last", "siguiente", "next"
  );

  public static String translateWeekId(String weekId) {
    return WEEK_ID_KEYWORDS_MAP.entrySet().stream()
      .map(param -> (Function<String, String>) s -> s.replace(param.getKey(), param.getValue()))
      .reduce(Function.identity(), Function::andThen)
      .apply(weekId);
  }
}
