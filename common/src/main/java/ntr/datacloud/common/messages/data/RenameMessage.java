package ntr.datacloud.common.messages.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class RenameMessage extends DataMessage {

    private String oldName;
    private String newName;

    @Override
    public String toString() {
        return super.toString() + " " +
                String.format("Rename file %s to %s",
                        oldName,
                        newName);
    }
}


