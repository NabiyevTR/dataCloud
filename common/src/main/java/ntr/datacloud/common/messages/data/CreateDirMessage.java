package ntr.datacloud.common.messages.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class CreateDirMessage extends DataMessage{

    private String newDir;

}
