package services;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import models.Chore;
import models.ChoreType;
import models.Ticket;
import models.User;
import models.WeeklyChores;
import repositories.ChoreManagementRepository;
import repositories.chores.ChoresRepository;
import repositories.choretypes.ChoreTypesRepository;
import repositories.tickets.TicketsRepository;
import repositories.users.UsersRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ChoreManagementServiceImp implements ChoreManagementService {
  private final ChoreManagementRepository repository;
  private final UsersRepository usersRepository;
  private final ChoreTypesRepository choreTypesRepository;
  private final ChoresRepository choresRepository;
  private final TicketsRepository ticketsRepository;

  @Inject
  public ChoreManagementServiceImp(ChoreManagementRepository repository, UsersRepository usersRepository,
                                   ChoreTypesRepository choreTypesRepository, ChoresRepository choresRepository,
                                   TicketsRepository ticketsRepository) {
    this.repository = repository;
    this.usersRepository = usersRepository;
    this.choreTypesRepository = choreTypesRepository;
    this.choresRepository = choresRepository;
    this.ticketsRepository = ticketsRepository;
  }

  @Override
  public CompletableFuture<List<Ticket>> listTickets(String userId) {
    return ticketsRepository.listTickets(userId);
  }

  @Override
  public CompletableFuture<List<WeeklyChores>> getWeeklyChores(String userId) {
    return choresRepository.listWeeklyChores(userId);
  }

  @Override
  public CompletableFuture<List<Chore>> listChores(String userId) {
    return choresRepository.listChores(userId);
  }

  @Override
  public CompletableFuture<Void> completeChore(String userId, String weekId, String choreType) {
    return choresRepository.completeChore(userId, weekId, choreType);
  }

  @Override
  public CompletableFuture<Void> skipWeek(String userId, String weekId) {
    return repository.skipWeek(userId, weekId);
  }

  @Override
  public CompletableFuture<Void> unSkipWeek(String userId, String weekId) {
    return repository.unSkipWeek(userId, weekId);
  }

  @Override
  public CompletableFuture<List<ChoreType>> listChoreTypes() {
    return choreTypesRepository.listChoreTypes();
  }

  @Override
  public CompletableFuture<WeeklyChores> createWeeklyChores(String weekId) {
    return choresRepository.createWeeklyChores(weekId);
  }

  @Override
  public CompletableFuture<List<User>> listUsers() {
    return usersRepository.listUsers();
  }
}
