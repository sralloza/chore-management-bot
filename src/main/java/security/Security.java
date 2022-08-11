package security;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import models.Tenant;
import services.ChoreManagementService;

import javax.inject.Inject;
import java.util.List;

@Slf4j
public class Security {
    private final ChoreManagementService service;
    private final boolean cached;
    private List<Tenant> tenants;

    @Inject
    public Security(ChoreManagementService service, Config config) {
        this.service = service;
        cached = config.getBoolean("api.users.cache");
        log.debug("Security is {}", cached ? "cached" : "not cached");
    }

    public boolean isAuthenticated(String tenantId) {
        if (!cached || tenants == null) {
            try {
                tenants = service.listTenants().get();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return tenants.stream()
                .peek(t -> {
                    System.out.println(t.getTenantId());
                    System.out.println(tenantId);
                })
            .anyMatch(t -> t.getTenantId().toString().equals(tenantId));
    }
}
