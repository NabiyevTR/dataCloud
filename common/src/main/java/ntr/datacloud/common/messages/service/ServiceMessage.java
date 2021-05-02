package ntr.datacloud.common.messages;


import lombok.Getter;
import lombok.Setter;

public class ServiceMessage extends Message{

    @Getter
    @Setter
    protected ServiceMessageStatus status = ServiceMessageStatus.NO_RESPONSE;

    public ServiceMessage() {
        this.type = MessageType.SERVICE;
    }
}
