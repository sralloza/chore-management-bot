package bot;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import constants.BotMessages;
import constants.UserMessages;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import models.CallbackQueryData;
import models.Chore;
import models.ChoreType;
import models.QueryType;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;
import security.Security;
import services.ChoreManagementService;
import services.MessagesService;
import services.RedisService;
import services.latex.LatexService;
import utils.Normalizers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static constants.BotMessages.UNDEFINED_COMMAND;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Slf4j
public class ChoreManagementBot extends BaseChoreManagementBot {
  private static final String WEEKLY_TASKS_TABLE_PNG = "weekly-tasks-table.png";
  private static final String TICKETS_TABLE_PNG = "tickets-table.png";

  @Getter
  private Integer menuMessage;

  @Inject
  public ChoreManagementBot(Config config, ChoreManagementService choreManagementService,
                            LatexService latexService, Security security, RedisService redisService,
                            MessagesService messagesService, Executor executor) {
    super(config, choreManagementService, security, latexService, redisService, messagesService, executor);
  }

  public Ability processMsg() {
    return Ability.builder()
      .name(DEFAULT)
      .action(ctx -> runCheckingUserRegistered(ctx, this::processMsg))
      .enableStats()
      .locality(USER)
      .privacy(PUBLIC)
      .build();
  }

  public Ability start() {
    return Ability.builder()
      .name("start")
      .info("Starts the bot")
      .locality(USER)
      .privacy(PUBLIC)
      .action(ctx -> runCheckingUserRegistered(ctx, this::sendMenuAsync))
      .enableStats()
      .build();
  }

  public Ability createWeeklyChores() {
    return Ability.builder()
      .name("create")
      .info("Creates the chores")
      .locality(USER)
      .input(1)
      .privacy(PUBLIC)
      .action(ctx -> {
        var chatId = ctx.chatId().toString();
        var weekId = ctx.update().getMessage().getText().split(getCommandRegexSplit())[1];
        if (chatId.equals(creatorId.toString())) {
          service.createWeeklyChores(weekId)
            .handleAsync((result, throwable) -> {
              if (throwable != null) {
                log.error("Error creating weekly chores", throwable);
                messagesHelper.handleException((Exception) throwable, chatId);
                return null;
              }
              messagesHelper.sendMessage("Tareas semanales creadas para la semana " + weekId, chatId, false);
              return null;
            }, executor);
        } else {
          messagesHelper.sendMessage("No tienes permiso para crear las tareas semanales", chatId, false);
        }
      })
      .enableStats()
      .build();
  }

  public boolean startFlowSelectTask(MessageContext ctx, List<Chore> tasks, List<ChoreType> choreTypes) {
    var chatId = ctx.chatId().toString();
    if (tasks.size() == 0) {
      messagesHelper.sendMessageMarkdown(BotMessages.NO_PENDING_TASKS, chatId);
      return false;
    }

    messagesHelper.removeBotQueryMessageIfExists(chatId, QueryType.COMPLETE_TASK);
    Map<String, String> choreTypeMap = choreTypes.stream()
      .collect(Collectors.toMap(ChoreType::getId, ChoreType::getName));

    var message = new SendMessage();
    var keyboard = new InlineKeyboardMarkup();
    keyboard.setKeyboard(tasks.stream()
      .map(chore -> {
        var keyb = new InlineKeyboardButton();
        var callbackData = new CallbackQueryData()
          .setType(QueryType.COMPLETE_TASK)
          .setWeekId(chore.getWeekId())
          .setChoreType(chore.getChoreTypeId());
        keyb.setText(chore.getWeekId() + " - " + choreTypeMap.get(chore.getChoreTypeId()));
        keyb.setCallbackData(callbackData.encode());
        return keyb;
      })
      .map(List::of)
      .collect(Collectors.toList()));
    message.setReplyMarkup(keyboard);
    message.setChatId(ctx.chatId().toString());
    message.setText(BotMessages.SELECT_TASK_TO_COMPLETE);
    try {
      var messageId = sender.execute(message).getMessageId();
      messagesService.saveMessageId(ctx.chatId(), QueryType.COMPLETE_TASK, messageId);
    } catch (TelegramApiException e) {
      messagesHelper.handleException(e, chatId, QueryType.COMPLETE_TASK);
    }

    return true;
  }

  private void processMsg(MessageContext ctx) {
    if (ctx.update().hasCallbackQuery()) {
      processQueryData(ctx);
      return;
    }
    if (ctx.update().getMessage().isReply()) {
      processReplyMsg(ctx);
      return;
    }

    String userMessage = ctx.update().getMessage().getText();
    String chatId = ctx.chatId().toString();
    log.debug("User message: {}", userMessage);

    CompletableFuture<List<ChoreType>> choreTypesFuture;

    switch (userMessage) {
      case UserMessages.TICKETS:
        choreTypesFuture = service.listChoreTypes();
        service.listTickets(chatId)
          .thenCombine(choreTypesFuture, Normalizers::normalizeTickets)
          .thenAcceptAsync(tickets -> sendTable(tickets, chatId, "ticketsTable", BotMessages.NO_TICKETS_FOUND), executor)
          .handleAsync(messagesHelper.exceptionHandler(chatId), executor);
        break;
      case UserMessages.TASKS:
        choreTypesFuture = service.listChoreTypes();
        service.getWeeklyChores(chatId)
          .thenCombine(choreTypesFuture, Normalizers::normalizeWeeklyChores)
          .thenAcceptAsync(tasks -> sendTable(tasks, chatId, "weeklyTasksTable", BotMessages.NO_TASKS), executor)
          .handleAsync(messagesHelper.exceptionHandler(chatId), executor);
        break;
      case UserMessages.COMPLETE_TASK:
        choreTypesFuture = service.listChoreTypes();
        service.listChores(chatId)
          .thenCombineAsync(choreTypesFuture, (choreList, choreTypeList) ->
            startFlowSelectTask(ctx, choreList, choreTypeList), executor)
          .handleAsync(messagesHelper.exceptionHandler(chatId), executor);
        break;
      case UserMessages.SKIP:
        silent.forceReply(BotMessages.ASK_FOR_WEEK_TO_SKIP, ctx.chatId());
        break;
      case UserMessages.UNSKIP:
        silent.forceReply(BotMessages.ASK_FOR_WEEK_TO_UNSKIP, ctx.chatId());
        break;
      default:
        messagesHelper.sendMessageMarkdown(UNDEFINED_COMMAND, chatId);
        break;
    }
  }

  private void processReplyMsg(MessageContext ctx) {
    String userMessage = ctx.update().getMessage().getText();
    var replyMsg = ctx.update().getMessage().getReplyToMessage().getText();
    var chatId = ctx.chatId().toString();

    switch (replyMsg) {
      case BotMessages.ASK_FOR_WEEK_TO_SKIP:
        service.skipWeek(chatId, userMessage)
          .handle(messagesHelper.replyHandler(ctx, "Week skipped: " + userMessage));
        break;
      case BotMessages.ASK_FOR_WEEK_TO_UNSKIP:
        service.unSkipWeek(chatId, userMessage)
          .handle(messagesHelper.replyHandler(ctx, "Week unskipped: " + userMessage));
        break;
      default:
        messagesHelper.sendMessage(UNDEFINED_COMMAND, chatId, false);
        break;
    }
  }

  private void processQueryData(MessageContext ctx) {
    String data = ctx.update().getCallbackQuery().getData();
    CallbackQueryData callbackData = CallbackQueryData.decode(data);
    String queryId = ctx.update().getCallbackQuery().getId();
    String chatId = ctx.chatId().toString();

    switch (callbackData.getType()) {
      case COMPLETE_TASK:
        service.completeChore(chatId, callbackData.getWeekId(), callbackData.getChoreType())
          .handleAsync(messagesHelper.callbackQueryHandler(ctx, queryId, BotMessages.TASK_COMPLETED, QueryType.COMPLETE_TASK), executor);
        break;
      default:
        messagesHelper.sendMessage(UNDEFINED_COMMAND, chatId, false);
        break;
    }
  }

  private void sendMenuAsync(MessageContext ctx) {
    SendMessage msg = new SendMessage();
    msg.setText(BotMessages.START_MSG);
    msg.disableNotification();
    msg.enableMarkdownV2(true);
    msg.setChatId(Long.toString(ctx.chatId()));
    msg.setReplyMarkup(Keyboards.getMainMenuKeyboard());

    SentCallback<Message> callback = new SentCallback<>() {
      @Override
      public void onResult(BotApiMethod method, Message response) {
        menuMessage = response.getMessageId();
      }

      @Override
      public void onError(BotApiMethod method, TelegramApiRequestException apiException) {
      }

      @Override
      public void onException(BotApiMethod method, Exception exception) {
      }
    };
    silent.executeAsync(msg, callback);
  }
}
