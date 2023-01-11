package services;

import models.SimpleChoreList;
import models.TenantList;
import models.TicketList;
import models.WeeklyChores;
import models.WeeklyChoresList;

import java.util.concurrent.CompletableFuture;


public interface ChoreManagementService {
    CompletableFuture<TicketList> getTickets(String userId);

    CompletableFuture<WeeklyChoresList> getWeeklyTasks(String userId);

    CompletableFuture<SimpleChoreList> getSimpleTasks(String userId);

    CompletableFuture<Void> completeTask(String userId, String weekId, String choreType);

    CompletableFuture<Void> skipWeek(String userId, String weekId);

    CompletableFuture<Void> unskipWeek(String userId, String weekId);

    CompletableFuture<WeeklyChores> createWeeklyChores(String userId, String weekId);

    CompletableFuture<TenantList> listUsersAdminToken();
}
