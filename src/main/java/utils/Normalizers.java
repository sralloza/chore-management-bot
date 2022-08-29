package utils;

import models.Chore;
import models.Ticket;
import models.WeeklyChores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Normalizers {
    public static List<List<String>> normalizeTickets(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            return Collections.emptyList();
        }

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
        return lines;
    }

    public static List<List<String>> normalizeWeeklyChores(List<WeeklyChores> weeklyChores) {
        if (weeklyChores.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<String>> lines = new ArrayList<>();
        lines.add(new ArrayList<>());
        lines.get(0).add("Week");

        for (Chore chore : weeklyChores.get(0).getChores()) {
            lines.get(0).add(chore.getType());
        }

        for (WeeklyChores weeklyChore : weeklyChores) {
            List<String> row = new ArrayList<>();
            row.add(weeklyChore.getWeekId());
            for (Chore chore : weeklyChore.getChores()) {
                String rowText = String.join(",", chore.getAssignedUsernames());
                if (!chore.getDone()) {
                    rowText = "*" + rowText + "*";
                }
                row.add(rowText);
            }
            lines.add(row);
        }
        return lines;
    }
}
