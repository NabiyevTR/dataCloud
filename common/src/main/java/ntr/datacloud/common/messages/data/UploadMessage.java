package ntr.datacloud.common.messages.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class UploadMessage extends DataMessage{

    private String fileName;
    private int fileSize; //todo delete if not necessary
    private byte[] content;

    @Override
    public String toString() {
        return super.toString() +
                String.format(
                       " File name = %s", fileName
                );
    }
}
