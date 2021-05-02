package ntr.datacloud.common.messages;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder (access = AccessLevel.PUBLIC)
@Getter
public class RegMessage extends ServiceMessage{

    private final String login;
    private final String password;


    @Override
    public String toString() {
        return  String.format("%s: status = %s login = %s; pass = %s,",
                getClass().getSimpleName() , status, login, password);
    }
}
