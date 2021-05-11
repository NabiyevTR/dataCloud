package ntr.datacloud.server.services.auth;

public interface AuthService {

    default String getRegexLogin() {
        return "^[a-z0-9_-]{3,16}$";
    }

    default String getRegexPass() {
        return "^[a-z0-9_-]{6,16}$";
    }

    boolean registration(String login, String password);

    boolean userExists(String login, String password);

    boolean changePass(String login, String oldPassword, String newPassword);

    void terminate();
}
