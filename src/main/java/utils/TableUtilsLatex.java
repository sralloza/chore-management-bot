package utils;

import models.Chore;
import models.Ticket;
import models.TicketList;
import models.WeeklyChores;
import models.WeeklyChoresList;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableUtilsLatex {
    public void genTicketsTable(TicketList tickets, String path) {
        List<List<String>> lines = new ArrayList<>();

        List<String> columns = new ArrayList<>();
        columns.add("Tenant");
        for (Ticket ticket : tickets) {
            columns.add(ticket.getId());
        }
        lines.add(columns);

        List<String> users = tickets.get(0).getTicketsByTenant().keySet().stream()
                .sorted().collect(Collectors.toList());

        for (String user : users) {
            List<String> row = new ArrayList<>();
            row.add(user);
            for (Ticket ticket : tickets) {
                row.add(ticket.getTicketsByTenant().get(user).toString());
            }
            lines.add(row);
        }

        genTable(lines, path);
    }

    public void genChoresTable(WeeklyChoresList weeklyChores, String path) {
        List<List<String>> lines = new ArrayList<>();
        lines.add(new ArrayList<>());
        lines.get(0).add("Week");

        for (Chore chore : weeklyChores.get(0).getChores()) {
            lines.get(0).add(chore.getType());
        }

        for (WeeklyChores weeklyChore : weeklyChores) {
            List<String> row = new ArrayList<>();
            row.add(weeklyChore.getWeekId());
            for (Chore chore: weeklyChore.getChores()) {
                row.add(String.join(",", chore.getAssignedUsernames()));
            }
            lines.add(row);
        }
        genTable(lines, path);
    }

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
