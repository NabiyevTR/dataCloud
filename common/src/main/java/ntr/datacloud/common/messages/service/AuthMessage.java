package ntr.datacloud.common.messages.service;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class AuthMessage extends ServiceMessage {


    protected final String login;
    protected final String password;

    @Override
    public String toString() {
        return  String.format("%s: status = %s login = %s pass = %s",
                getClass().getSimpleName() , status, login, password);
    }

}
