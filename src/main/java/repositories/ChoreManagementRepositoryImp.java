package repositories;

import com.google.inject.Inject;
import config.ConfigRepository;
import models.SimpleChoreList;
import models.TenantList;
import models.TicketList;
import models.WeeklyChores;
import models.WeeklyChoresList;
import security.Security;

import java.util.concurrent.CompletableFuture;

public class ChoreManagementRepositoryImp extends BaseRepository implements ChoreManagementRepository {
    @Inject
    public ChoreManagementRepositoryImp(ConfigRepository config, Security security) {
        super(config.getString("api.baseURL"), config.getString("api.token"), config, security);
    }

    public CompletableFuture<TicketList> getTickets(Long tenantId) {
        return sendGetRequest("/v1/tickets", TicketList.class, tenantId);
    }

    public CompletableFuture<WeeklyChoresList> getTasks(Long tenantId) {
        return sendGetRequest("/v1/weekly-chores?missingOnly=true", WeeklyChoresList.class, tenantId);
    }

    public CompletableFuture<Void> completeTask(Long tenantId, String weekId, String choreType) {
        String path = "/v1/weekly-chores/" + weekId + "/tenants/me/choreType/" + choreType + "/complete";
        return sendPostRequest(path, null, tenantId);
    }

    public CompletableFuture<SimpleChoreList> getSimpleTasks(Long tenantId) {
        return sendGetRequest("/v1/simple-chores", SimpleChoreList.class, tenantId);
    }

    public CompletableFuture<Void> skipWeek(Long tenantId, String weekId) {
        return sendPostRequest("/v1/tenants/me/skip/" + weekId, null, tenantId);
    }

    public CompletableFuture<Void> unskipWeek(Long tenantId, String weekId) {
        return sendPostRequest("/v1/tenants/me/unskip/" + weekId, null, tenantId);
    }

    public CompletableFuture<WeeklyChores> createWeeklyChores(Long tenantId, String weekId) {
        return sendPostRequest("/v1/weekly-chores/week/" + weekId, WeeklyChores.class, tenantId);
    }

    public CompletableFuture<TenantList> listTenantsAdminToken() {
        return sendGetRequestAdmin("/v1/tenants", TenantList.class);
    }
}
