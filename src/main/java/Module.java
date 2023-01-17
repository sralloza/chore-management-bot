import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import repositories.ChoreManagementRepository;
import repositories.ChoreManagementRepositoryImp;
import repositories.chores.ChoresRepositoryCacheableModule;
import repositories.choretypes.ChoreTypesRepositoryCacheableModule;
import repositories.tickets.TicketsRepositoryCacheableModule;
import repositories.users.UsersRepositoryCacheableModule;
import security.Security;
import security.SecurityImp;
import services.ChoreManagementService;
import services.ChoreManagementServiceImp;
import services.latex.LatexCacheableModule;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Module extends AbstractModule {
  @Override
  protected void configure() {
    install(new LatexCacheableModule());
    install(new ChoreTypesRepositoryCacheableModule());
    install(new UsersRepositoryCacheableModule());
    install(new ChoresRepositoryCacheableModule());
    install(new TicketsRepositoryCacheableModule());

    bind(Executor.class).toInstance(Executors.newCachedThreadPool());
    bind(ChoreManagementRepository.class).to(ChoreManagementRepositoryImp.class);
    bind(ChoreManagementService.class).to(ChoreManagementServiceImp.class);
    bind(Config.class).toInstance(ConfigFactory.load());
    bind(Security.class).to(SecurityImp.class);
  }
}
