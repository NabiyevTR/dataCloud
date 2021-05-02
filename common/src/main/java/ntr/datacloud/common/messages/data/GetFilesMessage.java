package ntr.datacloud.common.messages.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ntr.datacloud.common.filemanager.FileEntity;
import ntr.datacloud.common.messages.service.ServiceMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuperBuilder
@Getter
@Setter
public class GetFilesMessage extends DataMessage {

    @Override
    public String toString() {

        if (getFiles() == null) {
            return super.toString();
        } else {
            return super.toString() + "\nFiles: \n" +
                    getFiles()
                            .stream()
                            .map(f -> f.getName())
                            .collect(Collectors.joining("\n"))
                    + "\n";
        }


    }
}