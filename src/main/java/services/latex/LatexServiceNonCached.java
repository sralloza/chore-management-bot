package services.latex;

import lombok.extern.slf4j.Slf4j;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class LatexServiceNonCached extends BaseLatexService implements LatexService {
  @Override
  public File genTable(List<List<String>> data, String redisKeyPrefix) {
    String latex = "\\begin{tabular}{" + "c".repeat(data.get(0).size()) +
      "}\n" + data.stream()
      .map(line -> String.join(" & ", line))
      .collect(Collectors.joining(" \\\\ \n")) +
      "\n\\end{tabular}";

    TeXFormula formula;
    try {
      formula = new TeXFormula(latex);
    } catch (Exception e) {
      log.error("Error generating latex", e);
      throw new RuntimeException(e);
    }

    String filename = getFileName(redisKeyPrefix);
    formula.createPNG(TeXConstants.STYLE_DISPLAY, 200, filename, Color.white, Color.black);
    return new File(filename);
  }
}
