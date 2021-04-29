package ntr.datacloud.common.messages;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Builder(access = AccessLevel.PUBLIC)
@Getter
public class LogonMessage extends ServiceMessage {

    private enum Status {
        NO_RESPONSE,
        OK,
        INCORRECT_PASSWORD_OR_LOGIN,
        USER_IS_ALREADY_AUTHENTIFICATED,
        UNKNOWN_ERROR
    }

    private final String login;
    private final String password;
    private Status status = Status.NO_RESPONSE;

    @Override

    public String toString() {
        return String.format("%s: login = %s; pass = %s,",
                getClass().getSimpleName(), login, password);
    }

}