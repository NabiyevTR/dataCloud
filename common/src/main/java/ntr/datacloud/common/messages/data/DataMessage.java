package ntr.datacloud.common.messages.data;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ntr.datacloud.common.filemanager.FileEntity;
import ntr.datacloud.common.messages.Message;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class DataMessage extends Message {
    @Builder.Default
    protected DataMessageStatus status = DataMessageStatus.NO_RESPONSE;
    @Builder.Default
    protected String currentDir = "";
    private List<FileEntity> files;


    @Override
    public String toString() {
        return  String.format("%s: status = %s login = %s; pass = %s,",
                getClass().getSimpleName() , status, login, password);
    }
}