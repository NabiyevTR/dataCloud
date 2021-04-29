package ntr.datacloud.server.services.executors;

import lombok.extern.log4j.Log4j;
import ntr.datacloud.common.messages.LogonMessage;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.RegMessage;
import ntr.datacloud.server.services.auth.AuthService;
import ntr.datacloud.server.services.auth.JDBCAuthService;

@Log4j
public class ServiceServiceExecutor {

    private static AuthService authService = JDBCAuthService.getInstance();

    public static boolean execute(Message message) {
        if (message instanceof RegMessage) {
            return regUser((RegMessage)message);
        }

        if (message instanceof LogonMessage) {
            return logonUser((LogonMessage) message);
        }
        return false;
    }


    private static boolean logonUser(LogonMessage message) {
        if (authService.userExists(message.getLogin(),message.getPassword())) {
            //todo add to active users
            //todo send message auth success

            log.info("Find user!");
        }
        return false;
    }


    private static boolean regUser(RegMessage message) {
       return authService.registration(message.getLogin(), message.getPassword());
    }
}
