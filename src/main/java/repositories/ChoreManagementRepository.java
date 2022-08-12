package repositories;

import models.SimpleChoreList;
import models.TenantList;
import models.TicketList;
import models.WeeklyChores;
import models.WeeklyChoresList;

import java.util.concurrent.CompletableFuture;

public interface ChoreManagementRepository {
    CompletableFuture<TicketList> getTickets(Long tenantId);

    CompletableFuture<WeeklyChoresList> getTasks(Long tenantId);

    CompletableFuture<Void> completeTask(Long tenantId, String weekId, String choreType);

    CompletableFuture<SimpleChoreList> getSimpleTasks(Long tenantId);

    CompletableFuture<Void> skipWeek(Long tenantId, String weekId);

    CompletableFuture<Void> unskipWeek(Long tenantId, String weekId);

    CompletableFuture<WeeklyChores> createWeeklyChores(Long tenantId, String weekId);

    CompletableFuture<TenantList> listTenantsAdminToken();
}
