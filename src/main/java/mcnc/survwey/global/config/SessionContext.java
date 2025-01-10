package mcnc.survwey.global.config;

public class SessionContext {

    /**
     * 사용자 세션 정보 저장
     * @Author 이건희
     */
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(String userId) {
        currentUser.set(userId);
    }

    public static String getCurrentUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}
