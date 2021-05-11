package ntr.datacloud.common.messages.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class UploadMessage extends DataMessage {

    private String fileName;
    private byte[] content;
    @Builder.Default
    private boolean last = false;

    @Override
    public String toString() {
        return super.toString() +
                String.format(
                        " File name = %s", fileName
                );
    }
}
