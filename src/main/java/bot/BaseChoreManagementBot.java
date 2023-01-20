package bot;

import com.typesafe.config.Config;
import helpers.MessagesHelper;
import lombok.extern.slf4j.Slf4j;
import models.QueryType;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import security.Security;
import services.ChoreManagementService;
import services.MessagesService;
import services.RedisService;
import services.latex.LatexService;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static org.telegram.abilitybots.api.db.MapDBContext.offlineInstance;

@Slf4j
public abstract class BaseChoreManagementBot extends AbilityBot {
  protected final ChoreManagementService service;
  protected final Security security;
  protected final LatexService latexService;
  protected final RedisService redisService;
  protected final MessagesService messagesService;
  protected final Executor executor;
  protected final MessagesHelper messagesHelper;
  protected final Long creatorId;

  protected BaseChoreManagementBot(Config config, ChoreManagementService service,
                                   Security security, LatexService tableUtils, RedisService redisService,
                                   MessagesService messagesService, Executor executor) {
    super(config.getString("telegram.bot.token"), config.getString("telegram.bot.username"), offlineInstance("db"));
    this.service = service;
    this.security = security;
    this.latexService = tableUtils;
    this.redisService = redisService;
    this.messagesService = messagesService;
    this.executor = executor;
    this.creatorId = config.getLong("telegram.creatorID");
    this.messagesHelper = new MessagesHelper(sender, messagesService, creatorId);
  }

  @Override
  public long creatorId() {
    return creatorId;
  }

  protected void runCheckingUserRegistered(MessageContext ctx, Consumer<MessageContext> consumer) {
    var chatId = ctx.chatId().toString();
    security.isAuthenticated(chatId)
      .thenAcceptAsync(isAuthenticated -> {
        if (isAuthenticated) {
          consumer.accept(ctx);
        } else {
          messagesHelper.sendMessage("No tienes permiso para utilizar este bot", chatId, false);
        }
      }, executor)
      .handleAsync(messagesHelper.exceptionHandler(chatId));
  }

  protected void sendTable(List<List<String>> table,
                           String chatId,
                           String keyPrefix,
                           String emptyMessage) {
    if (table.isEmpty()) {
      messagesHelper.sendMessage(emptyMessage, chatId, false);
      return;
    }
    File file = latexService.genTable(table, keyPrefix);

    try {
      InputFile inputFile = new InputFile(file);
      SendPhoto message = new SendPhoto();
      message.setPhoto(inputFile);
      message.setChatId(chatId);
      this.execute(message);

      boolean result = file.delete();
      if (!result) {
        log.error("Could not delete file {}", file.getAbsolutePath());
      }
    } catch (Exception exc) {
      messagesHelper.handleException(exc, chatId);
    }
  }

  protected void answerCallbackQuery(String queryId, String chatId) {
    var answer = new AnswerCallbackQuery();
    answer.setCallbackQueryId(queryId);
    try {
      sender.execute(answer);
    } catch (TelegramApiException e) {
      messagesHelper.handleException(e, chatId);
      log.error("Error answering callback query", e);
    }
  }
}
