package utils;

import models.ChoreType;
import models.Ticket;
import models.WeeklyChore;
import models.WeeklyChores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Normalizers {
  public static List<List<String>> normalizeTickets(List<Ticket> tickets, List<ChoreType> choreTypes) {
    if (tickets.isEmpty()) {
      return Collections.emptyList();
    }

    Map<String, String> choreTypeMap = choreTypes.stream()
      .collect(Collectors.toMap(ChoreType::getId, ChoreType::getName));

    List<List<String>> lines = new ArrayList<>();
    List<String> columns = new ArrayList<>();
    columns.add("Usuario");
    for (Ticket ticket : tickets) {
      columns.add(choreTypeMap.get(ticket.getId()));
    }
    lines.add(columns);

    List<String> users = tickets.get(0).getTicketsByUserName().keySet().stream()
      .sorted().collect(Collectors.toList());

    for (String user : users) {
      List<String> row = new ArrayList<>();
      row.add(user);
      for (Ticket ticket : tickets) {
        row.add(ticket.getTicketsByUserName().get(user).toString());
      }
      lines.add(row);
    }
    return lines;
  }

  public static List<List<String>> normalizeWeeklyChores(List<WeeklyChores> weeklyChores, List<ChoreType> choreTypes) {
    Map<String, String> choreTypeMap = choreTypes.stream()
      .collect(Collectors.toMap(ChoreType::getId, ChoreType::getName));
    if (weeklyChores.isEmpty()) {
      return Collections.emptyList();
    }

    List<List<String>> lines = new ArrayList<>();
    lines.add(new ArrayList<>());
    lines.get(0).add("Semana");

    for (WeeklyChore chore : weeklyChores.get(0).getChores()) {
      var name = choreTypeMap.get(chore.getChoreTypeId());
      lines.get(0).add(name);
    }

    for (WeeklyChores weeklyChore : weeklyChores) {
      List<String> row = new ArrayList<>();
      row.add(weeklyChore.getWeekId());
      for (WeeklyChore chore : weeklyChore.getChores()) {
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
