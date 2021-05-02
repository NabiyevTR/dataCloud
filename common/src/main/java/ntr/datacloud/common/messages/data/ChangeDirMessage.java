package ntr.datacloud.common.messages.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ntr.datacloud.common.filemanager.FileEntity;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class ChangeDirMessage extends DataMessage{
    private String relPath;

}
