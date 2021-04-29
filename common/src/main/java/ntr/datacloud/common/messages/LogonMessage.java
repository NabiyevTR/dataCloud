package ntr.datacloud.common.messages;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Builder(access = AccessLevel.PUBLIC)
@Getter
public class LogonMessage extends ServiceMessage {



    private final String login;
    private final String password;

    @Override
    public String toString() {
        return String.format("%s: login = %s; pass = %s,",
                getClass().getSimpleName(), login, password);
    }

}