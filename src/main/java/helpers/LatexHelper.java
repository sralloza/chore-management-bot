package helpers;

import constants.BotMessages;
import lombok.extern.slf4j.Slf4j;
import models.ChoreType;
import models.Ticket;
import models.WeeklyChores;
import services.latex.LatexService;
import utils.Normalizers;

import java.io.File;
import java.util.List;

@Slf4j
public class LatexHelper {
  private final BotHelper botHelper;
  private final LatexService latexService;

  public LatexHelper(BotHelper botHelper, LatexService latexService) {
    this.botHelper = botHelper;
    this.latexService = latexService;
  }

  public boolean sendTicketsTable(List<Ticket> tickets, List<ChoreType> choreTypes, String chatId) {
    List<List<String>> table = Normalizers.normalizeTickets(tickets, choreTypes);
    sendTable(table, chatId, "ticketsTable", BotMessages.NO_TICKETS_FOUND);
    return true;
  }

  public boolean sendWeeklyChoresTable(List<WeeklyChores> weeklyChores, List<ChoreType> choreTypes, String chatId) {
    List<List<String>> table = Normalizers.normalizeWeeklyChores(weeklyChores, choreTypes);
    sendTable(table, chatId, "weeklyTasksTable", BotMessages.NO_TASKS);
    return true;
  }

  protected void sendTable(List<List<String>> table,
                           String chatId,
                           String keyPrefix,
                           String emptyMessage) {
    if (table.isEmpty()) {
      botHelper.sendMessage(emptyMessage, chatId, false);
      return;
    }
    File file = latexService.genTable(table, keyPrefix);

    try {
      botHelper.sendImage(chatId, file);
    } finally {
      boolean result = file.delete();
      if (!result) {
        log.error("Could not delete file {}", file.getAbsolutePath());
      }
    }
  }
}
