package ntr.datacloud.common.messages;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
@Getter
public class Message implements Serializable {


    protected final String login;
    protected final String password;
    @Setter
    protected String errorText;


}
