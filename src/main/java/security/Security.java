package security;

public interface Security {
    String getTenantToken(String userId);

    boolean isAuthenticated(String userId);
}
