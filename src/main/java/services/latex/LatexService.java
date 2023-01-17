package services.latex;

import java.io.File;
import java.util.List;

public interface LatexService {
  File genTable(List<List<String>> data, String redisKeyPrefix);
  String getFileName(String redisKeyPrefix);
}
