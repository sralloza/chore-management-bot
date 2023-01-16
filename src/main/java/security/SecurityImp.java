package security;

import com.google.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import models.User;
import services.ChoreManagementService;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Singleton
public class SecurityImp implements Security {
  private final ChoreManagementService service;

  @Inject
  public SecurityImp(ChoreManagementService service) {
    this.service = service;
  }

  public CompletableFuture<String> getUserApiKey(String userId) {
    return getUsers()
      .thenApply(users -> users.stream()
        .filter(user -> user.getId().equals(userId))
        .findFirst()
        .map(User::getApiKey)
        .orElse(null));
  }

  public CompletableFuture<Boolean> isAuthenticated(String userId) {
    return getUsers()
      .thenApply(users -> users.stream()
        .anyMatch(t -> t.getId().equals(userId)));
  }

  private CompletableFuture<List<User>> getUsers() {
    return service.listUsers();
  }
}
