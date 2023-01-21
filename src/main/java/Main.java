import bot.ChoreManagementBot;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
public class Main {
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new MainModule());
    ChoreManagementBot bot = injector.getInstance(ChoreManagementBot.class);

    try {
      TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
      telegramBotsApi.registerBot(bot);
    } catch (TelegramApiException e) {
      log.error("Error starting bot", e);
    }
  }
}
