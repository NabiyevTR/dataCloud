package ntr.datacloud.common.messages;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
public class Message implements Serializable {

    protected MessageType type =null;

    @Override
    public String toString() {
        return type.toString();
    }
}
