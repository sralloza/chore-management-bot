package repositories.users;

import models.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UsersRepository {
  CompletableFuture<List<User>> listUsers();
}
