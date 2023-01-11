package security;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import models.Tenant;
import services.ChoreManagementService;

import javax.inject.Inject;
import java.util.List;

@Slf4j
public class SecurityImp implements Security {
    private final ChoreManagementService service;
    private final boolean cached;
    private List<Tenant> userList;

    @Inject
    public SecurityImp(ChoreManagementService service, Config config) {
        this.service = service;
        cached = config.getBoolean("api.users.cache");
        log.debug("Security is {}", cached ? "cached" : "not cached");
    }

    public String getTenantToken(String tenantId) {
        return getUsers().stream()
                .filter(t -> t.getId().equals(tenantId))
                .findFirst()
                .map(Tenant::getApiToken)
                .orElse(null);
    }

    public boolean isAuthenticated(String tenantId) {
        return getUsers().stream()
            .anyMatch(t -> t.getId().equals(tenantId));
    }

    private List<Tenant> getUsers() {
        if (!cached || userList == null) {
            try {
                userList = service.listUsersAdminToken().get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return userList;
    }
}
