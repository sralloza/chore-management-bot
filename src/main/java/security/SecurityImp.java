package security;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import models.User;
import services.ChoreManagementService;

import javax.inject.Inject;
import java.util.List;

@Slf4j
public class SecurityImp implements Security {
    private final ChoreManagementService service;
    private final boolean cached;
    private List<User> userList;

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
                .map(User::getApiKey)
                .orElse(null);
    }

    public boolean isAuthenticated(String tenantId) {
        return getUsers().stream()
            .anyMatch(t -> t.getId().equals(tenantId));
    }

    private List<User> getUsers() {
        if (!cached || userList == null) {
            try {
                userList = service.listUsersAdminToken().get();
            } catch (Exception e) {
                log.error("Error getting users", e);
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return userList;
    }
}
