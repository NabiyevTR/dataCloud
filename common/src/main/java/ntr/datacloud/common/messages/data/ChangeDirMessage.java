package ntr.datacloud.common.messages.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class ChangeDirMessage extends DataMessage {
    private String relPath;

    @Override
    public String toString() {
        return super.toString() + " " +
                String.format("Change dir to %s",
                        relPath);
    }
}
