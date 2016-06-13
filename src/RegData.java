import java.util.concurrent.ConcurrentHashMap;

public final class RegData {
    private static ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Long> lastActivity = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> accessCode = new ConcurrentHashMap<>();
    private static final BackUpHandler backUpHandler = new BackUpHandler();
    private static final RegDataCleanUp regDataCleanUp = new RegDataCleanUp();

    private RegData() {}

    public static ConcurrentHashMap<String, String> getUsers() {
        return users;
    }

    public static ConcurrentHashMap<String, Long> getLastActivity() {
        return lastActivity;
    }

    public static ConcurrentHashMap<String, String> getAccessCode() {
        return accessCode;
    }

    public static void setUsers(ConcurrentHashMap<String, String> users) {
        RegData.users = users;
    }
}
