package bot;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import constants.Messages;
import constants.UserMessages;
import lombok.Getter;
import lombok.SneakyThrows;
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
import services.RedisService;
import services.latex.LatexService;
import utils.Normalizers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.CREATOR;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Slf4j
public class ChoreManagementBot extends BaseChoreManagementBot {
  private static final String WEEKLY_TASKS_TABLE_PNG = "weekly-tasks-table.png";
  private static final String TICKETS_TABLE_PNG = "tickets-table.png";

  private final Long creatorId;
  private final Keyboards keyboards;

  @Getter
  private Integer menuMessage;

  @Inject
  public ChoreManagementBot(Config config,
                            Keyboards keyboards,
                            ChoreManagementService choreManagementService,
                            LatexService latexService, Security security, RedisService redisService) {
    super(config.getString("telegram.bot.token"), config.getString("telegram.bot.username"),
      keyboards, choreManagementService, security, latexService, redisService);

    creatorId = config.getLong("telegram.creatorID");
    this.keyboards = keyboards;
  }

  @Override
  public long creatorId() {
    return creatorId;
  }

  public Ability processMsg() {
    return Ability.builder()
      .name(DEFAULT)
      .action(this::processMsg)
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
      .action(this::sendMenuAsync)
      .enableStats()
      .build();
  }

  public Ability createWeeklyChores() {
    return Ability.builder()
      .name("create")
      .info("Creates the chores")
      .locality(USER)
      .input(1)
      .privacy(CREATOR)
      .action(ctx -> {
        // TODO: set privacy to PUBLIC with manual check
        var chatId = ctx.chatId().toString();
        var weekId = ctx.update().getMessage().getText().split(getCommandRegexSplit())[1];
        try {
          service.createWeeklyChores(chatId, weekId).get();
          sendMessage("Weekly chores created for week " + weekId, chatId, false);
        } catch (Exception e) {
          handleException(e, chatId);
        }
      })
      .enableStats()
      .build();
  }

  public void startFlowSelectTask(MessageContext ctx, List<Chore> tasks, List<ChoreType> choreTypes,
                                  QueryType callbackDataId, String taskSelectorMsg) {
    var chatId = ctx.chatId().toString();
    if (tasks.size() == 0) {
      sendMessageMarkdown(Messages.NO_PENDING_TASKS, chatId);
      return;
    }

    Optional.ofNullable(redisService.getMessage(chatId, callbackDataId))
      .ifPresent(messageId -> deleteMessage(chatId, messageId));

    Map<String, String> choreTypeMap = choreTypes.stream()
      .collect(Collectors.toMap(ChoreType::getId, ChoreType::getName));

    var message = new SendMessage();
    var keyboard = new InlineKeyboardMarkup();
    keyboard.setKeyboard(tasks.stream()
      .map(chore -> {
        var keyb = new InlineKeyboardButton();
        var callbackData = new CallbackQueryData()
          .setType(callbackDataId)
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
    message.setText(taskSelectorMsg);
    try {
      var messageId = sender.execute(message).getMessageId();
      redisService.saveMessage(ctx.chatId(), callbackDataId, messageId);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  @SneakyThrows
  private void processMsg(MessageContext ctx) {
    if (!requireUser(ctx)) {
      return;
    }
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

    switch (userMessage) {
      case UserMessages.TICKETS:
        List<ChoreType> choreTypes1 = service.getChoreTypes().get();
        service.getTickets(chatId)
          .thenApply(tickets1 -> Normalizers.normalizeTickets(tickets1, choreTypes1))
          .thenAccept(tickets -> sendTable(tickets, chatId, TICKETS_TABLE_PNG, Messages.NO_TICKETS_FOUND));
        break;
      case UserMessages.TASKS:
        List<ChoreType> choreTypes2 = service.getChoreTypes().get();
        service.getWeeklyChores(chatId)
          .thenApply(weeklyChores -> Normalizers.normalizeWeeklyChores(weeklyChores, choreTypes2))
          .thenAccept(tasks -> sendTable(tasks, chatId, WEEKLY_TASKS_TABLE_PNG, Messages.NO_TASKS));
        break;
      case UserMessages.COMPLETE_TASK:
        List<ChoreType> choreTypes3 = service.getChoreTypes().get();
        service.getChores(chatId)
          .thenAccept(chores -> startFlowSelectTask(ctx, chores, choreTypes3, QueryType.COMPLETE_TASK,
            Messages.SELECT_TASK_TO_COMPLETE));
        break;
      case UserMessages.SKIP:
        silent.forceReply(Messages.ASK_FOR_WEEK_TO_SKIP, ctx.chatId());
        break;
      case UserMessages.UNSKIP:
        silent.forceReply(Messages.ASK_FOR_WEEK_TO_UNSKIP, ctx.chatId());
        break;
      default:
        sendMessageMarkdown("Undefined command", chatId);
        break;
    }
  }

  private void processReplyMsg(MessageContext ctx) {
    String userMessage = ctx.update().getMessage().getText();
    var replyMsg = ctx.update().getMessage().getReplyToMessage().getText();
    var chatId = ctx.chatId().toString();

    switch (replyMsg) {
      case Messages.ASK_FOR_WEEK_TO_SKIP:
        service.skipWeek(chatId, userMessage)
          .handle(replyHandler(ctx, "Week skipped: " + userMessage));
        break;
      case Messages.ASK_FOR_WEEK_TO_UNSKIP:
        service.unskipWeek(chatId, userMessage)
          .handle(replyHandler(ctx, "Week unskipped: " + userMessage));
        break;
      default:
        sendMessage(Messages.UNDEFINED_COMMAND, chatId, false);
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
        service.completeTask(chatId, callbackData.getWeekId(), callbackData.getChoreType())
          .handle(callbackQueryHandler(ctx, queryId, Messages.TASK_COMPLETED, QueryType.COMPLETE_TASK));
        break;
      default:
        sendMessage(Messages.UNDEFINED_COMMAND, chatId, false);
        break;
    }
  }

  private void sendMenuAsync(MessageContext ctx) {
    if (!requireUser(ctx)) {
      return;
    }
    SendMessage msg = new SendMessage();
    msg.setText(Messages.START_MSG);
    msg.disableNotification();
    msg.enableMarkdownV2(true);
    msg.setChatId(Long.toString(ctx.chatId()));
    msg.setReplyMarkup(keyboards.getMainMenuKeyboard());

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
