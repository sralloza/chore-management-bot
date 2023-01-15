package services.latex;

import lombok.extern.slf4j.Slf4j;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import services.latex.LatexService;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class LatexServiceNonCached implements LatexService {
  @Override
  public void genTable(List<List<String>> data, String path) {
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

    formula.createPNG(TeXConstants.STYLE_DISPLAY, 200, path, Color.white, Color.black);
  }
}
