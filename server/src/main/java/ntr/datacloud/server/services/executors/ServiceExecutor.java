package ntr.datacloud.server.services.executors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.common.filemanager.FileManager;
import ntr.datacloud.common.filemanager.FileManagerImpl;
import ntr.datacloud.common.messages.service.*;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.server.ConnectedClients;
import ntr.datacloud.server.ServerProperties;
import ntr.datacloud.server.services.auth.AuthService;
import ntr.datacloud.server.services.auth.JDBCAuthService;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j
public class ServiceExecutor {

    private static final AuthService authService = JDBCAuthService.getInstance();
    private static final ServerProperties properties = ServerProperties.getInstance();

    private static final Map<String, Consumer<Message>> executors =
            new HashMap<>();

    private static ConnectedClients clients = ConnectedClients.getInstance();

    static {
        executors.put(LogonMessage.class.getName(), logonExecutor());
        executors.put(LogoutMessage.class.getName(), logoutExecutor());
        executors.put(RegMessage.class.getName(), regExecutor());
        executors.put(ConfigMessage.class.getName(), configExecutor());
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
                // path create directory for new user
                Path path = Paths.get(
                        ServerProperties.getInstance().getRootDir().toAbsolutePath().normalize().toString()
                );
                try {
                    new FileManagerImpl(path.toString()).createDir(regMessage.getLogin());
                    regMessage.setStatus(ServiceMessageStatus.OK);
                } catch (Exception e) {
                    log.error("Cannot create root dir for client with login " + regMessage.getLogin(), e);
                    regMessage.setStatus(ServiceMessageStatus.LOGIN_IS_NOT_AVAILABLE);
                    regMessage.setErrorText(e.getMessage());
                }
            } else {
                regMessage.setStatus(ServiceMessageStatus.LOGIN_IS_NOT_AVAILABLE);
            }
        };
    }

    private static Consumer<Message> configExecutor() {
        return message -> {
            ConfigMessage configMessage = (ConfigMessage) message;
            configMessage.setMaxFileFrame(properties.getMaxFileFrame());
            configMessage.setRegexLogin(authService.getRegexLogin());
            configMessage.setRegexPass(authService.getRegexPass());
            configMessage.setStatus(ServiceMessageStatus.OK);
        };
    }


}
