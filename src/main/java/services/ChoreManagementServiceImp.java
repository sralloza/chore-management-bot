package services;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import models.SimpleChoreList;
import models.TenantList;
import models.TicketList;
import models.WeeklyChores;
import models.WeeklyChoresList;
import repositories.ChoreManagementRepository;

import java.util.concurrent.CompletableFuture;


@Slf4j
public class ChoreManagementServiceImp implements ChoreManagementService{
    private final ChoreManagementRepository repository;

    @Inject
    public ChoreManagementServiceImp(ChoreManagementRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<TicketList> getTickets(String userId) {
        return repository.getTickets(userId);
    }

    public CompletableFuture<WeeklyChoresList> getWeeklyTasks(String userId) {
        return repository.getTasks(userId);
    }

    public CompletableFuture<SimpleChoreList> getSimpleTasks(String userId) {
        return repository.getSimpleTasks(userId);
    }

    public CompletableFuture<Void> completeTask(String userId, String weekId, String choreType) {
        return repository.completeTask(userId, weekId, choreType);
    }

    public CompletableFuture<Void> skipWeek(String userId, String weekId) {
        return repository.skipWeek(userId, weekId);
    }

    public CompletableFuture<Void> unskipWeek(String userId, String weekId) {
        return repository.unskipWeek(userId, weekId);
    }

    public CompletableFuture<WeeklyChores> createWeeklyChores(String userId, String weekId) {
        return repository.createWeeklyChores(userId, weekId);
    }

    public CompletableFuture<TenantList> listUsersAdminToken() {
        return repository.listUsersAdminToken();
    }
}
