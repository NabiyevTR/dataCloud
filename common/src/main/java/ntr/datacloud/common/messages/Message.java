package ntr.datacloud.common.messages;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
@Getter
public class Message implements Serializable {

    @Setter
    protected String errorText;

}
