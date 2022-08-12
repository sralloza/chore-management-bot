import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import repositories.ChoreManagementRepository;
import repositories.ChoreManagementRepositoryImp;
import security.Security;
import security.SecurityImp;
import services.ChoreManagementService;
import services.ChoreManagementServiceImp;

public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(ChoreManagementRepository.class).to(ChoreManagementRepositoryImp.class);
        bind(ChoreManagementService.class).to(ChoreManagementServiceImp.class);
        bind(Config.class).toInstance(ConfigFactory.load());
        bind(Security.class).to(SecurityImp.class);
    }
}
