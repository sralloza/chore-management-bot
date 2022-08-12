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
    private List<Tenant> tenants;

    @Inject
    public SecurityImp(ChoreManagementService service, Config config) {
        this.service = service;
        cached = config.getBoolean("api.users.cache");
        log.debug("Security is {}", cached ? "cached" : "not cached");
    }

    public String getTenantToken(Long tenantId) {
        return getTenants().stream()
                .filter(t -> t.getTenantId().equals(tenantId))
                .findFirst()
                .map(Tenant::getApiToken)
                .orElse(null);
    }

    public boolean isAuthenticated(String tenantId) {
        return getTenants().stream()
            .anyMatch(t -> t.getTenantId().toString().equals(tenantId));
    }

    private List<Tenant> getTenants() {
        if (!cached || tenants == null) {
            try {
                tenants = service.listTenantsAdminToken().get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return tenants;
    }
}
