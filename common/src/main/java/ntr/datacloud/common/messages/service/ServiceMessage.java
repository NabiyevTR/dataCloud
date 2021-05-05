package ntr.datacloud.common.messages.service;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ntr.datacloud.common.messages.Message;


@Getter
@Setter
@SuperBuilder
public class ServiceMessage extends Message {
    @Builder.Default
    protected ServiceMessageStatus status = ServiceMessageStatus.NO_RESPONSE;

    @Override
    public String toString() {
        return  String.format("%s: status = %s",
                getClass().getSimpleName() );
    }
}
