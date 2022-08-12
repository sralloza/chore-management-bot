package security;

public interface Security {
    String getTenantToken(Long tenantId);

    boolean isAuthenticated(String tenantId);
}
