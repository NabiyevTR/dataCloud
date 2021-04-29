package ntr.datacloud.common.messages;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder (access = AccessLevel.PUBLIC)
@Getter
public class RegMessage extends ServiceMessage{

    private enum Status {
        NO_RESPONSE,
        OK,
        LOGIN_IS_NOT_AVAILABLE,
        UNKNOWN_ERROR
    }

    private final String login;
    private final String password;
    @Setter
    private Status status = Status.NO_RESPONSE;


    @Override
    public String toString() {
        return  String.format("%s: login = %s; pass = %s,",
                getClass().getSimpleName() , login, password);
    }
}
