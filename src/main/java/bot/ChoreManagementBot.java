package bot;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import constants.Messages;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import models.SimpleChoreList;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;
import security.Security;
import services.ChoreManagementService;
import utils.Normalizers;
import utils.TableUtilsLatex;

import java.util.List;
import java.util.stream.Collectors;

import static constants.Messages.ASK_FOR_WEEK_TO_SKIP;
import static constants.Messages.ASK_FOR_WEEK_TO_UNSKIP;
import static constants.Messages.COMPLETE_TASK;
import static constants.Messages.NO_PENDING_TASKS;
import static constants.Messages.NO_TASKS;
import static constants.Messages.NO_TICKETS_FOUND;
import static constants.Messages.SKIP;
import static constants.Messages.TASKS;
import static constants.Messages.TICKETS;
import static constants.Messages.UNSKIP;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.CREATOR;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Slf4j
public class ChoreManagementBot extends BaseChoreManagementBot {
    private static final String WEEKLY_TASKS_TABLE_PNG = "weekly-tasks-table.png";
    private static final String TICKETS_TABLE_PNG = "tickets-table.png";
    private static final String SEPARATOR = "/";

    private final Long creatorId;
    private final Keyboards keyboards;

    @Getter
    private Integer menuMessage;

    @Inject
    public ChoreManagementBot(Config config,
                              Keyboards keyboards,
                              ChoreManagementService choreManagementService,
                              TableUtilsLatex tableUtils, Security security) {
        super(config.getString("telegram.bot.token"),
            config.getString("telegram.bot.username"),
            keyboards,
            choreManagementService,
            security,
            tableUtils);

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
                var weekId = ctx.update().getMessage().getText().split(getCommandRegexSplit())[1];
                try {
                    service.createWeeklyChores(ctx.chatId(), weekId).get();
                    sendMessage("Weekly chores created for week " + weekId, ctx.chatId(), false);
                } catch (Exception e) {
                    handleException(e, ctx.chatId());
                }
            })
            .enableStats()
            .build();
    }

    public void startCompleteTaskFlow(MessageContext ctx) {
        SimpleChoreList tasks;
        try {
            tasks = service.getSimpleTasks(ctx.chatId()).get();
        } catch (Exception e) {
            handleException(e, ctx.chatId());
            return;
        }
        if (tasks.size() == 0) {
            sendMessageMarkdown(NO_PENDING_TASKS, ctx.chatId());
            return;
        }
        var message = new SendMessage();
        var keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(tasks.stream()
            .map(chore -> {
                var keyb = new InlineKeyboardButton();
                var s = chore.getWeekId() + " - " + chore.getChoreType();
                keyb.setText(s);
                keyb.setCallbackData("COMPLETE_TASK" + SEPARATOR + chore.getWeekId() +
                    SEPARATOR + chore.getChoreType());
                return keyb;
            })
            .map(List::of)
            .collect(Collectors.toList()));
        message.setReplyMarkup(keyboard);
        message.setChatId(ctx.chatId().toString());
        message.setText(Messages.SELECT_TASK);
        silent.execute(message);
    }

    @SneakyThrows
    private void processMsg(MessageContext ctx) {
        if (!requireTenant(ctx)) {
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
        Long chatId = ctx.chatId();
        log.debug("User message: {}", userMessage);

        switch (userMessage) {
            case TICKETS:
                service.getTickets(chatId)
                    .thenAccept(tickets -> sendTable(tickets,
                        Normalizers::normalizeTickets,
                        chatId,
                        TICKETS_TABLE_PNG,
                        NO_TICKETS_FOUND));
                break;
            case TASKS:
                service.getWeeklyTasks(chatId)
                    .thenAccept(tasks -> sendTable(tasks,
                        Normalizers::normalizeWeeklyChores,
                        chatId,
                        WEEKLY_TASKS_TABLE_PNG,
                        NO_TASKS));
                break;
            case COMPLETE_TASK:
                startCompleteTaskFlow(ctx);
                break;
            case SKIP:
                silent.forceReply(ASK_FOR_WEEK_TO_SKIP, ctx.chatId());
                break;
            case UNSKIP:
                silent.forceReply(ASK_FOR_WEEK_TO_UNSKIP, ctx.chatId());
                break;
            default:
                sendMessageMarkdown("Undefined command", ctx.chatId());
                break;
        }
    }

    private void processReplyMsg(MessageContext ctx) {
        String userMessage = ctx.update().getMessage().getText();
        var replyMsg = ctx.update().getMessage().getReplyToMessage().getText();

        switch (replyMsg) {
            case ASK_FOR_WEEK_TO_SKIP:
                try {
                    service.skipWeek(ctx.chatId(), userMessage).get();
                } catch (Exception e) {
                    handleException(e, ctx.chatId());
                    return;
                }
                sendMessage("Week skipped: " + userMessage, ctx.chatId(), false);
                break;
            case ASK_FOR_WEEK_TO_UNSKIP:
                try {
                    service.unskipWeek(ctx.chatId(), userMessage).get();
                } catch (Exception e) {
                    handleException(e, ctx.chatId());
                    return;
                }
                sendMessage("Week unskipped: " + userMessage, ctx.chatId(), false);
                break;
            default:
                sendMessage(Messages.UNDEFINED_COMMAND, ctx.chatId(), false);
                break;
        }
    }

    private void processQueryData(MessageContext ctx) {
        String data = ctx.update().getCallbackQuery().getData();
        String queryId = ctx.update().getCallbackQuery().getId();

        String[] dataParts = data.split(SEPARATOR);
        switch (dataParts[0]) {
            case "COMPLETE_TASK":
                service.completeTask(ctx.chatId(), dataParts[1], dataParts[2])
                    .thenAccept(unused -> {
                        answerCallbackQuery(queryId);
                        sendMessage(Messages.TASK_COMPLETED, ctx.chatId(), false);
                    }).exceptionally(e -> {
                        answerCallbackQuery(queryId);
                        handleException((Exception) e, ctx.chatId());
                        return null;
                    });
                break;
            default:
                sendMessage(Messages.UNDEFINED_COMMAND, ctx.chatId(), false);
                break;
        }
    }

    private void sendMenuAsync(MessageContext ctx) {
        if (!requireTenant(ctx)) {
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
