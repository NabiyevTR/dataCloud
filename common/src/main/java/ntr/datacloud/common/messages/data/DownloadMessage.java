package ntr.datacloud.common.messages.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ntr.datacloud.common.messages.Message;

@Getter
@Setter
@SuperBuilder
public class DownloadMessage extends DataMessage {

    private String fileName;
    private int fileSize;
    private byte[] content;

}
