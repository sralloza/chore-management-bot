package services.latex;

import java.util.List;

public interface LatexService {
  void genTable(List<List<String>> data, String path);
}
