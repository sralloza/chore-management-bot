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
        super(config.getString("api.baseURL"), config.getString("api.adminApiKey"), config, security);
    }

    public CompletableFuture<TicketList> getTickets(String userId) {
        return sendGetRequest("/api/v1/tickets", TicketList.class, userId);
    }

    public CompletableFuture<WeeklyChoresList> getTasks(String userId) {
        return sendGetRequest("/api/v1/weekly-chores?missing_only=true", WeeklyChoresList.class, userId);
    }

    public CompletableFuture<Void> completeTask(String userId, String weekId, String choreType) {
        String path = "/api/v1/weekly-chores/" + weekId + "/chore-type/" + choreType + "/complete";
        return sendPostRequest(path, null, userId);
    }

    public CompletableFuture<SimpleChoreList> getSimpleTasks(String userId) {
        return sendGetRequest("/api/v1/chores?user_id=me&done=false", SimpleChoreList.class, userId);
    }

    public CompletableFuture<Void> skipWeek(String userId, String weekId) {
        return sendPostRequest("/api/v1/users/me/deactivate/" + weekId, null, userId);
    }

    public CompletableFuture<Void> unskipWeek(String userId, String weekId) {
        return sendPostRequest("/api/v1/users/me/reactivate/" + weekId, null, userId);
    }

    public CompletableFuture<WeeklyChores> createWeeklyChores(String userId, String weekId) {
        return sendPostRequest("/api/v1/weekly-chores/week/" + weekId, WeeklyChores.class, userId);
    }

    public CompletableFuture<TenantList> listUsersAdminToken() {
        return sendGetRequestAdmin("/api/v1/users", TenantList.class);
    }
}
