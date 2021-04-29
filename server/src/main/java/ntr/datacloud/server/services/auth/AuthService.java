package ntr.datacloud.server.services.auth;

import java.io.Closeable;

public interface AuthService  {

    boolean registration(String login, String password);

    boolean userExists(String login, String password);

    boolean changePass(String login, String oldPassword, String newPassword);

    void terminate();
}
