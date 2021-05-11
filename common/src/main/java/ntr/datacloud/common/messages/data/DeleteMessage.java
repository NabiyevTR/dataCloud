package ntr.datacloud.common.messages.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class DeleteMessage extends DataMessage{

    private String fileToDelete;

    @Override
    public String toString() {
        return super.toString() + " " +
                String.format("Delete file %s",
                       fileToDelete);
    }
}
