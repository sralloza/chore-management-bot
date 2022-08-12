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

    public CompletableFuture<TicketList> getTickets(Long tenantId) {
        return repository.getTickets(tenantId);
    }

    public CompletableFuture<WeeklyChoresList> getWeeklyTasks(Long tenantId) {
        return repository.getTasks(tenantId);
    }

    public CompletableFuture<SimpleChoreList> getSimpleTasks(Long tenantId) {
        return repository.getSimpleTasks(tenantId);
    }

    public CompletableFuture<Void> completeTask(Long tenantId, String weekId, String choreType) {
        return repository.completeTask(tenantId, weekId, choreType);
    }

    public CompletableFuture<Void> skipWeek(Long tenantId, String weekId) {
        return repository.skipWeek(tenantId, weekId);
    }

    public CompletableFuture<Void> unskipWeek(Long tenantId, String weekId) {
        return repository.unskipWeek(tenantId, weekId);
    }

    public CompletableFuture<WeeklyChores> createWeeklyChores(Long tenantId, String weekId) {
        return repository.createWeeklyChores(tenantId, weekId);
    }

    public CompletableFuture<TenantList> listTenantsAdminToken() {
        return repository.listTenantsAdminToken();
    }
}
