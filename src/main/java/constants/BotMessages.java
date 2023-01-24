package constants;

public class BotMessages {
  public static final String START_MSG = "`Conexión establecida con el servidor`";

  public static final String NO_TASKS = "No hay tareas definidas";
  public static final String NO_PENDING_TASKS = "No hay tareas sin completar";
  public static final String SELECT_TASK_TO_COMPLETE = "Selecciona la tarea a completar";

  public static final String PERMISSION_DENIED = "No tienes permiso para utilizar este bot";

  public static final String TASK_COMPLETED = "Tarea completada";
  public static final String UNDEFINED_COMMAND = "Comando indefinido";

  public static final String EXTRA_WEEK_IDS = "Puedes utilizar también las palabras clave *actual*, *anterior* y *siguiente*.";
  public static final String ASK_FOR_WEEK_TO_SKIP = "Escribe la semana que quieres saltar (ej: 2022.03)\n" + EXTRA_WEEK_IDS;
  public static final String ASK_FOR_WEEK_TO_UNSKIP = "Escribe la semana que quieres restablecer (ej: 2022.03)\n" + EXTRA_WEEK_IDS;

  public static final String ASK_FOR_WEEK_TO_SKIP_MD_SAFE = ASK_FOR_WEEK_TO_SKIP.
    replace("(", "\\(").replace(")", "\\)").replace(".", "\\.");
  public static final String ASK_FOR_WEEK_TO_UNSKIP_MD_SAFE = ASK_FOR_WEEK_TO_UNSKIP.
    replace("(", "\\(").replace(")", "\\)").replace(".", "\\.");

  public static final String ASK_FOR_WEEK_TO_SKIP_RAW = ASK_FOR_WEEK_TO_SKIP.replace("*", "");
  public static final String ASK_FOR_WEEK_TO_UNSKIP_RAW = ASK_FOR_WEEK_TO_UNSKIP.replace("*", "");

  public static final String UNKNOWN_ERROR = "Ha ocurrido un error no contemplado\\. Se ha enviado más" +
    " información al administrador para resolver el problema\\.";
  public static final String NO_TICKETS_FOUND = "No se han encontrado tickets";

  public static final String NOT_IMPLEMENTED = "Comando no implementado";

  public static final String WEEK_SKIPPED = "Semana saltada: %s";
  public static final String WEEK_UNSKIPPED = "Semana restaurada: %s";

  public static final String WEEKLY_CHORES_CREATED = "Tareas semanales creadas para la semana %s";
  public static final String WEEKLY_CHORES_FORBIDDEN_CREATE = "No tienes permiso para crear las tareas semanales";
}
