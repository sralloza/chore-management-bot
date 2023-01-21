package security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import models.User;
import repositories.users.UsersRepository;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Singleton
public class SecurityImp implements Security {
  private final UsersRepository usersRepository;

  @Inject
  public SecurityImp(UsersRepository usersRepository) {
    this.usersRepository = usersRepository;
  }

  public CompletableFuture<String> getUserApiKey(String userId) {
    return usersRepository.listUsers()
      .thenApply(users -> users.stream()
        .filter(user -> user.getId().equals(userId))
        .findFirst()
        .map(User::getApiKey)
        .orElseThrow(() -> new RuntimeException("User not found")));
  }

  public CompletableFuture<Boolean> isAuthenticated(String userId) {
    return usersRepository.listUsers()
      .thenApply(users -> users.stream()
        .anyMatch(t -> t.getId().equals(userId)));
  }
}
