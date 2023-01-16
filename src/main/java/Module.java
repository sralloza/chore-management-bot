import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import repositories.ChoreManagementRepository;
import repositories.ChoreManagementRepositoryImp;
import repositories.chores.ChoresRepositoryCacheableModule;
import repositories.choretypes.ChoreTypesRepositoryCacheableModule;
import repositories.users.UsersRepositoryCacheableModule;
import security.Security;
import security.SecurityImp;
import services.ChoreManagementService;
import services.ChoreManagementServiceImp;
import services.latex.LatexCacheableModule;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Module extends AbstractModule {
  @Override
  protected void configure() {
    install(new LatexCacheableModule());
    install(new ChoreTypesRepositoryCacheableModule());
    install(new UsersRepositoryCacheableModule());
    install(new ChoresRepositoryCacheableModule());

    Config config = ConfigFactory.load();
    String botName = config.getString("telegram.bot.username");
    ThreadFactory namedThreadFactory =
      new ThreadFactoryBuilder().setNameFormat(botName + " Telegram Executor").build();

    bind(Executor.class).toInstance(Executors.newSingleThreadExecutor(namedThreadFactory));
    bind(ChoreManagementRepository.class).to(ChoreManagementRepositoryImp.class);
    bind(ChoreManagementService.class).to(ChoreManagementServiceImp.class);
    bind(Config.class).toInstance(config);
    bind(Security.class).to(SecurityImp.class);
  }
}
