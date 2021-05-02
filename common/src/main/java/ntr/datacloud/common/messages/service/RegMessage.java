package ntr.datacloud.common.messages.service;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
public class RegMessage extends ServiceMessage {


    @Override
    public String toString() {
        return  String.format("%s: status = %s login = %s; pass = %s,",
                getClass().getSimpleName() , status, login, password);
    }
}
