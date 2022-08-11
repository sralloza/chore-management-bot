import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(Config.class).toInstance(ConfigFactory.load());
    }
}
