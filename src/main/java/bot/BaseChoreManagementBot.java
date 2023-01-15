package bot;

import constants.Messages;
import exceptions.APIException;
import lombok.extern.slf4j.Slf4j;
import models.QueryType;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import security.Security;
import services.ChoreManagementService;
import services.RedisService;
import services.latex.LatexService;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.telegram.abilitybots.api.db.MapDBContext.offlineInstance;

@Slf4j
public abstract class BaseChoreManagementBot extends AbilityBot {
  private final Keyboards keyboards;
  protected final ChoreManagementService service;
  protected final Security security;
  protected final LatexService latexService;
  protected final RedisService redisService;

  protected BaseChoreManagementBot(String botToken, String botUsername, Keyboards keyboards,
                                   ChoreManagementService service, Security security,
                                   LatexService tableUtils, RedisService redisService) {
    super(botToken, botUsername, offlineInstance("db"));
    this.keyboards = keyboards;
    this.service = service;
    this.security = security;
    this.latexService = tableUtils;
    this.redisService = redisService;
  }

  protected void sendMessageMarkdown(String msgStr, String chatId) {
    sendMessage(msgStr, chatId, true);
  }

  protected void sendMessage(String msgStr, String chatId, boolean markdown) {
    SendMessage msg = new SendMessage();
    if (markdown) {
      msgStr = msgStr.replace("-", "\\-").replace("|", "\\|");
      msg.enableMarkdownV2(true);
      log.debug("Sending message with markdownV2 enabled: {}", msgStr);
    }

    msg.setText(msgStr);
    msg.setChatId(chatId);
    msg.setReplyMarkup(keyboards.getMainMenuKeyboard());

    var r = silent.execute(msg);
    if (r.isEmpty()) {
      System.err.println("Error sending message");
    }
  }

  protected boolean requireUser(MessageContext ctx) {
    var chatId = ctx.chatId().toString();
    if (!security.isAuthenticated(chatId)) {
      sendMessage("You don't have permission to execute this action", chatId, false);
      return false;
    }
    return true;
  }

  protected void handleException(Exception e, String chatId) {
    handleException(e, chatId, null);
  }

  protected void handleException(Exception e, String chatId, QueryType type) {
    log.error("Manually handling exception", e);
    Optional.ofNullable(redisService.getMessage(chatId, type))
      .ifPresent(messageId -> deleteMessage(chatId, messageId));

    if (e.getClass().equals(APIException.class)) {
      var exc = (APIException) e;
      sendMessage("Error: " + exc.getMsg(), chatId, false);
    } else if (e.getCause().getClass().equals(APIException.class)) {
      var exc = (APIException) e.getCause();
      sendMessage("Error: " + exc.getMsg(), chatId, false);
    } else {
      sendMessage(Messages.UNKNOWN_ERROR, chatId, true);
      String msg = "ERROR:\n" + e.getClass() + " - " + e.getMessage();
      sendMessage(msg, String.valueOf(creatorId()), false);
    }
  }

  protected void sendTable(List<List<String>> table,
                           String chatId,
                           String filename,
                           String emptyMessage) {
    if (table.isEmpty()) {
      sendMessage(emptyMessage, chatId, false);
      return;
    }
    latexService.genTable(table, filename);

    try {
      InputFile inputFile = new InputFile(new File(filename));
      SendPhoto message = new SendPhoto();
      message.setPhoto(inputFile);
      message.setChatId(chatId);
      this.execute(message);

      var result = new File(filename).delete();
      log.debug("Deleting file {} result: {}", filename, result);
    } catch (Exception exc) {
      handleException(exc, chatId);
    }
  }

  protected void answerCallbackQuery(String queryId) {
    var answer = new AnswerCallbackQuery();
    answer.setCallbackQueryId(queryId);
    var result = silent.execute(answer);
    System.out.println(result);
  }

  protected void deleteMessage(String chatId, Integer messageId) {
    log.debug("Deleting message {} in chat {}", messageId, chatId);
    var message = new DeleteMessage();
    message.setMessageId(messageId);
    message.setChatId(chatId);
    silent.execute(message);
  }

  protected void editMessage(Long chatId, Integer messageId, String text) {
    log.debug("Editing message {} in chat {} with text '{}'", messageId, chatId, text);
    var message = new EditMessageText();
    message.setMessageId(messageId);
    message.setChatId(chatId);
    message.setText(text);
    silent.execute(message);
  }

  protected BiFunction<Void, Throwable, Void> callbackQueryHandler(MessageContext ctx, String queryId,
                                                                   String messageOk, QueryType type) {
    var chatId = ctx.chatId().toString();
    return (unused, e) -> {
      answerCallbackQuery(queryId);
      if (e != null) {
        handleException((Exception) e, chatId, type);
      } else {
        var messageId = redisService.getMessage(chatId, type);
        editMessage(ctx.chatId(), messageId, messageOk);
      }
      return null;
    };
  }

  protected BiFunction<Void, Throwable, Void> replyHandler(MessageContext ctx, String messageOk) {
    var chatId = ctx.chatId().toString();
    return (unused, e) -> {
      if (e != null) {
        handleException((Exception) e, chatId);
      } else {
        sendMessage(messageOk, chatId, false);
      }
      return null;
    };
  }
}
