package utils;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class TableUtilsLatex {
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
            System.err.println("Exception: " + e);
            return;
        }

        formula.createPNG(TeXConstants.STYLE_DISPLAY, 200, path, Color.white, Color.black);
    }
}
