import java.util.concurrent.ConcurrentHashMap;

public final class RegData {
    private static ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Long> lastActivity = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> accessCode = new ConcurrentHashMap<>();
    private static final long MAX_TIME_OUT = 1800000;   // 30 min

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

    public static long getMaxTimeOut() {
        return MAX_TIME_OUT;
    }
}
