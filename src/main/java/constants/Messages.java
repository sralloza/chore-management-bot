package constants;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

public class Messages {
    public static final String START_MSG = "*API connected*";

    public static final String NO_TASKS = "No tasks found";
    public static final String NO_PENDING_TASKS = "No pending tasks found";
    public static final String SELECT_TASK_TO_COMPLETE = "Select task to complete";

    public static final Callback DEFAULT_CALLBACK = new Callback();
    public static final String TASK_COMPLETED = "Task completed";
    public static final String UNDEFINED_COMMAND = "Undefined command";
    public static final String ASK_FOR_WEEK_TO_SKIP = "Write week id to skip (year.number)";
    public static final String ASK_FOR_WEEK_TO_UNSKIP = "Write week id to unskip (year.number)";
    public static final String UNKNOWN_ERROR = "Unknown error happened\\. More info sent to the creator\\.";
    public static final String NO_TICKETS_FOUND = "No tickets found";

    private static class Callback implements SentCallback<Boolean> {
        @Override
        public void onResult(BotApiMethod method, Boolean response) {
        }

        @Override
        public void onError(BotApiMethod method, TelegramApiRequestException apiException) {
        }

        @Override
        public void onException(BotApiMethod method, Exception exception) {
        }
    }
}
