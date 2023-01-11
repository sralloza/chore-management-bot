package repositories;

import models.SimpleChoreList;
import models.TenantList;
import models.TicketList;
import models.WeeklyChores;
import models.WeeklyChoresList;

import java.util.concurrent.CompletableFuture;

public interface ChoreManagementRepository {
    CompletableFuture<TicketList> getTickets(String userId);

    CompletableFuture<WeeklyChoresList> getTasks(String userId);

    CompletableFuture<Void> completeTask(String userId, String weekId, String choreType);

    CompletableFuture<SimpleChoreList> getSimpleTasks(String userId);

    CompletableFuture<Void> skipWeek(String userId, String weekId);

    CompletableFuture<Void> unskipWeek(String userId, String weekId);

    CompletableFuture<WeeklyChores> createWeeklyChores(String userId, String weekId);

    CompletableFuture<TenantList> listUsersAdminToken();
}
