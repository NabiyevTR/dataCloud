package ntr.datacloud.common.messages.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.stream.Collectors;

@SuperBuilder
@Getter
@Setter
public class GetFilesMessage extends DataMessage {

    @Builder.Default
    protected String relDir = "";
}