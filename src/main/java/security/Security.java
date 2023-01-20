package security;

import java.util.concurrent.CompletableFuture;

public interface Security {
  CompletableFuture<String> getUserApiKey(String userId);

  CompletableFuture<Boolean> isAuthenticated(String userId);
}
