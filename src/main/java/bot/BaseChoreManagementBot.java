package bot;

import com.typesafe.config.Config;
import constants.BotMessages;
import helpers.BotHelper;
import helpers.LatexHelper;
import lombok.extern.slf4j.Slf4j;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import security.Security;
import services.ChoreManagementService;
import services.MessagesService;
import services.RedisService;
import services.latex.LatexService;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static org.telegram.abilitybots.api.db.MapDBContext.offlineInstance;

@Slf4j
public abstract class BaseChoreManagementBot extends AbilityBot {
  protected final ChoreManagementService service;
  protected final Security security;
  protected final RedisService redisService;
  protected final MessagesService messagesService;
  protected final Executor executor;
  protected final BotHelper helper;
  protected final Long creatorId;
  protected final LatexHelper latexHelper;

  protected BaseChoreManagementBot(Config config, ChoreManagementService service,
                                   Security security, LatexService latexService, RedisService redisService,
                                   MessagesService messagesService, Executor executor) {
    super(config.getString("telegram.bot.token"), config.getString("telegram.bot.username"), offlineInstance("db"));
    this.service = service;
    this.security = security;
    this.redisService = redisService;
    this.messagesService = messagesService;
    this.executor = executor;
    this.creatorId = config.getLong("telegram.creatorID");
    this.helper = new BotHelper(this, messagesService, creatorId);
    this.latexHelper = new LatexHelper(this.helper, latexService);
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
          helper.sendMessage(BotMessages.PERMISSION_DENIED, chatId, false);
        }
      }, executor)
      .handleAsync(helper.exceptionHandler(chatId));
  }

  protected void answerCallbackQuery(String queryId, String chatId) {
    var answer = new AnswerCallbackQuery();
    answer.setCallbackQueryId(queryId);
    try {
      sender.execute(answer);
    } catch (TelegramApiException e) {
      log.error("Error answering callback query", e);
      helper.handleException(e, chatId);
    }
  }
}
