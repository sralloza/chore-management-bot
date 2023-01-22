package helpers;

import bot.Keyboards;
import constants.BotMessages;
import exceptions.APIException;
import lombok.extern.slf4j.Slf4j;
import models.QueryType;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import services.MessagesService;

import java.io.File;
import java.util.function.BiFunction;

@Slf4j
public class BotHelper {
  private final AbilityBot bot;
  private final MessageSender sender;
  private final MessagesService messagesService;
  private final long creatorId;

  public BotHelper(AbilityBot bot, MessagesService messagesService, long creatorId) {
    this.bot = bot;
    this.sender = bot.sender();
    this.messagesService = messagesService;
    this.creatorId = creatorId;
  }

  public void sendMessage(String msgStr, String chatId, boolean markdown) {
    SendMessage msg = new SendMessage();
    if (markdown) {
      msgStr = msgStr.replace("-", "\\-").replace("|", "\\|");
      msg.enableMarkdownV2(true);
    }

    msg.setText(msgStr);
    msg.setChatId(chatId);
    msg.setReplyMarkup(Keyboards.getMainMenuKeyboard());

    try {
      sender.execute(msg);
    } catch (TelegramApiException e) {
      handleException(e, chatId);
    }
  }

  public void sendImage(String chatId, File file) {
    InputFile inputFile = new InputFile(file);
    SendPhoto message = new SendPhoto();
    message.setPhoto(inputFile);
    message.setChatId(chatId);
    try {
      bot.execute(message);
    } catch (TelegramApiException e) {
      handleException(e, chatId);
    }
  }

  private void deleteMessage(String chatId, Integer messageId) {
    log.debug("Deleting message {} in chat {}", messageId, chatId);
    var message = new DeleteMessage();
    message.setMessageId(messageId);
    message.setChatId(chatId);
    try {
      sender.execute(message);
    } catch (TelegramApiException e) {
      log.warn("Error deleting message", e);
    }
  }

  private void editMessage(Long chatId, Integer messageId, String text) {
    log.debug("Editing message {} in chat {} with text '{}'", messageId, chatId, text);
    var message = new EditMessageText();
    message.setMessageId(messageId);
    message.setChatId(chatId);
    message.setText(text);
    try {
      sender.execute(message);
    } catch (TelegramApiException e) {
      log.error("Error editing message", e);
      handleException(e, chatId.toString());
    }
  }

  public void removeBotQueryMessageIfExists(String chatId, QueryType type) {
    messagesService.getMessageId(chatId, type)
      .ifPresent(messageId -> {
        deleteMessage(chatId, messageId);
        messagesService.deleteMessageId(chatId, type);
      });
  }

  private void sendUnknownError(Exception e, String chatId) {
    sendMessage(BotMessages.UNKNOWN_ERROR, chatId, true);
    String msg = "ERROR IN CHAT WITH " + chatId + ":\n" + e.getClass() + " - " + e.getMessage();
    sendMessage(msg, String.valueOf(creatorId), false);
  }

  protected void answerCallbackQuery(String queryId, String chatId) {
    var answer = new AnswerCallbackQuery();
    answer.setCallbackQueryId(queryId);
    try {
      sender.execute(answer);
    } catch (TelegramApiException e) {
      handleException(e, chatId);
      log.error("Error answering callback query", e);
    }
  }

  public BiFunction<Void, Throwable, Void> callbackQueryHandler(MessageContext ctx, String queryId,
                                                                String messageOk, QueryType type) {
    var chatId = ctx.chatId().toString();
    return (unused, e) -> {
      answerCallbackQuery(queryId, chatId);
      if (e != null) {
        handleException((Exception) e, chatId, type);
      } else {
        messagesService.getMessageId(chatId, type)
          .ifPresent(messageId -> {
            editMessage(ctx.chatId(), messageId, messageOk);
            messagesService.deleteMessageId(chatId, type);
          });
      }
      return null;
    };
  }

  public BiFunction<Void, Throwable, Void> replyHandler(MessageContext ctx, String messageOk) {
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

  public void handleException(Exception e, String chatId) {
    handleException(e, chatId, null);
  }

  protected static boolean isApiExceptionNormalForUser(APIException exc) {
    if (exc.getStatusCode().equals(404) && exc.getMsg().equalsIgnoreCase("Not Found")) {
      return false;
    }
    return exc.getStatusCode() < 500;
  }

  public void handleException(Exception e, String chatId, QueryType type) {
    log.error("Manually handling exception", e);
    removeBotQueryMessageIfExists(chatId, type);

    Exception realException = e;
    boolean isApiException = false;
    if (e instanceof APIException) {
      isApiException = true;
    } else if (e.getCause() instanceof APIException) {
      isApiException = true;
      realException = (Exception) e.getCause();
    }
    if (isApiException) {
      APIException exc = (APIException) realException;
      if (!isApiExceptionNormalForUser(exc)) {
        sendUnknownError(e, chatId);
        return;
      }
      sendMessage("Error: " + exc.getMsg(), chatId, false);
    } else {
      sendUnknownError(e, chatId);
    }
  }

  public <T> BiFunction<T, Throwable, T> exceptionHandler(String chatId) {
    return (T obj, Throwable throwable) -> {
      if (throwable != null) {
        handleException((Exception) throwable, chatId);
      }
      return obj;
    };
  }
}
