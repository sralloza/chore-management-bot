package constants;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

public class BotMessages {
  public static final String START_MSG = "```Conexión establecida```";

  public static final String NO_TASKS = "No hay tareas definidas";
  public static final String NO_PENDING_TASKS = "No hay tareas pendientes";
  public static final String SELECT_TASK_TO_COMPLETE = "Selecciona la tarea a completar";

  public static final Callback DEFAULT_CALLBACK = new Callback();
  public static final String TASK_COMPLETED = "Tarea completada";
  public static final String UNDEFINED_COMMAND = "Comando indefinido";
  public static final String ASK_FOR_WEEK_TO_SKIP = "Escribe la semana que quieres saltar (ej: 2022.03)";
  public static final String ASK_FOR_WEEK_TO_UNSKIP = "Write week id to unskip (year.number)";
  public static final String UNKNOWN_ERROR = "Ha ocurrido un error no contemplado\\. Se ha enviado más" +
    " información al administrador para resolver el problema\\.";
  public static final String NO_TICKETS_FOUND = "No se han encontrado tickets";

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
