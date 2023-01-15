import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import repositories.ChoreManagementRepository;
import repositories.ChoreManagementRepositoryImp;
import security.Security;
import security.SecurityImp;
import services.ChoreManagementService;
import services.ChoreManagementServiceImp;
import services.latex.LatexCacheableModule;

public class Module extends AbstractModule {
    @Override
    protected void configure() {
        install(new LatexCacheableModule());

        bind(ChoreManagementRepository.class).to(ChoreManagementRepositoryImp.class);
        bind(ChoreManagementService.class).to(ChoreManagementServiceImp.class);
        bind(Config.class).toInstance(ConfigFactory.load());
        bind(Security.class).to(SecurityImp.class);
    }
}
