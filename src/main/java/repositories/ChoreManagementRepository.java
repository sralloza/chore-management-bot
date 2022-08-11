package repositories;

import com.google.inject.Inject;
import config.ConfigRepository;
import models.SimpleChoreList;
import models.TenantList;
import models.TicketList;
import models.WeeklyChores;
import models.WeeklyChoresList;

import java.util.concurrent.CompletableFuture;

public class ChoreManagementRepository extends BaseRepository {
    @Inject
    public ChoreManagementRepository(ConfigRepository config) {
        super(config.getString("api.baseURL"), config.getString("api.token"), config);
    }

    public CompletableFuture<TicketList> getTickets() {
        return sendRequest("/v1/tickets", TicketList.class, "admin");
    }

    public CompletableFuture<WeeklyChoresList> getTasks() {
        return sendRequest("/v1/weekly-chores?missing_only=true", WeeklyChoresList.class, "admin");
    }

    public CompletableFuture<Void> completeTask(String weekId, Integer tenantId, String choreType) {
        String path = "/v1/weekly-chores/" + weekId + "/tenant/" + tenantId + "/choreType/" + choreType + "/complete";
        return sendRequest("POST", path, null, "admin");
    }

    public CompletableFuture<SimpleChoreList> getSimpleTasks(Integer tenantId) {
        return sendRequest("/v1/simple-chores?missing_only=true&tenant_id=" + tenantId, SimpleChoreList.class, "admin");
    }

    public CompletableFuture<Void> skipWeek(Integer tenantId, String weekId) {
        return sendRequest("POST", "/v1/tenants/" + tenantId + "/skip/" + weekId, null, "admin");
    }

    public CompletableFuture<Void> unskipWeek(Integer tenantId, String weekId) {
        return sendRequest("POST", "/v1/tenants/" + tenantId + "/unskip/" + weekId, null, "admin");
    }

    public CompletableFuture<WeeklyChores> createWeeklyChores(String weekId) {
        return sendRequest("POST", "/v1/weekly-chores/week/" + weekId, WeeklyChores.class, "admin");
    }

    public CompletableFuture<TenantList> getTenants() {
        return sendRequest("GET", "/v1/tenants", TenantList.class, "admin");
    }
}
