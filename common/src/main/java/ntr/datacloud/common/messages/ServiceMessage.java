package ntr.datacloud.common.messages;


public class ServiceMessage extends Message{
    public ServiceMessage() {
        this.type = MessageType.SERVICE;
    }
}
