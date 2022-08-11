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
public class ChoreManagementService {
    private final ChoreManagementRepository repository;

    @Inject
    public ChoreManagementService(ChoreManagementRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<TicketList> getTickets() {
        return repository.getTickets();
    }

    public CompletableFuture<WeeklyChoresList> getWeeklyTasks() {
        return repository.getTasks();
    }

    public CompletableFuture<SimpleChoreList> getSimpleTasks(Integer tenantId) {
        return repository.getSimpleTasks(tenantId);
    }

    public CompletableFuture<Void> completeTask(String weekId, Integer tenantId, String choreType) {
        return repository.completeTask(weekId, tenantId, choreType);
    }

    public CompletableFuture<Void> skipWeek(Integer tenantId, String weekId) {
        return repository.skipWeek(tenantId, weekId);
    }

    public CompletableFuture<Void> unskipWeek(Integer tenantId, String weekId) {
        return repository.unskipWeek(tenantId, weekId);
    }

    public CompletableFuture<WeeklyChores> createWeeklyChores(String weekId) {
        return repository.createWeeklyChores(weekId);
    }

    public CompletableFuture<TenantList> listTenants() {
        return repository.getTenants();
    }
}
