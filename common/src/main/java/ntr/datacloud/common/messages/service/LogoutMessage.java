package ntr.datacloud.common.messages.service;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class LogoutMessage extends ServiceMessage {


    @Override
    public String toString() {
        return  String.format("%s: status = %s",
                getClass().getSimpleName() , status);
    }



}
