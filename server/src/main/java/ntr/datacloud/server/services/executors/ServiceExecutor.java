package ntr.datacloud.server.services.executors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.common.messages.service.LogonMessage;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.service.LogoutMessage;
import ntr.datacloud.common.messages.service.RegMessage;
import ntr.datacloud.common.messages.service.ServiceMessageStatus;
import ntr.datacloud.server.ConnectedClients;
import ntr.datacloud.server.services.auth.AuthService;
import ntr.datacloud.server.services.auth.JDBCAuthService;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j
public class ServiceExecutor {

    private static final AuthService authService = JDBCAuthService.getInstance();

    private static final Map<String, Consumer<Message>> executors =
            new HashMap<>();

    private static ConnectedClients clients = ConnectedClients.getInstance();

    static {
        executors.put(LogonMessage.class.getName(), logonExecutor());
        executors.put(LogoutMessage.class.getName(), logoutExecutor());
        executors.put(RegMessage.class.getName(), regExecutor());
    }


    public static boolean execute(Message message) {
        String key = message.getClass().getName();
        if (executors.containsKey(key)) {
            executors.get(key).accept(message);
            return true;
        } else {
            return false;
        }
    }

    private static Consumer<Message> logonExecutor() {
        return message -> {
            LogonMessage logonMessage = (LogonMessage) message;
            if (authService.userExists(logonMessage.getLogin(), logonMessage.getPassword())) {
                logonMessage.setStatus(ServiceMessageStatus.OK);

            } else {
                logonMessage.setStatus(ServiceMessageStatus.INCORRECT_PASSWORD_OR_LOGIN);
            }
        };
    }

    private static Consumer<Message> logoutExecutor() {
        return message -> {
            LogoutMessage logonMessage = (LogoutMessage) message;
            logonMessage.setStatus(ServiceMessageStatus.OK);
        };
    }

    private static Consumer<Message> regExecutor() {
        return message -> {
            RegMessage regMessage = (RegMessage) message;
            if (authService.registration(regMessage.getLogin(), regMessage.getPassword())) {
                regMessage.setStatus(ServiceMessageStatus.OK);
            } else {
                regMessage.setStatus(ServiceMessageStatus.LOGIN_IS_NOT_AVAILABLE);
            }
        };
    }






}
