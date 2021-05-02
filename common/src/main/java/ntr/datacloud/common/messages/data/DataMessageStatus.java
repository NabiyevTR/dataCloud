package ntr.datacloud.common.messages.data;

import lombok.Getter;

public enum DataMessageStatus  {
    NO_RESPONSE("No response from server."),
    OK("Operation completed successfully."),
    UNKNOWN_ERROR("Unknown error."),
    CANNOT_GET_FILES_FROM_SERVER("Cannot get files from server."),
    NO_SUCH_DIRECTORY("No such directory"),
    ACCESS_DENIED("Access denied."),
    UNKNOWN_COMMAND("Unknown command."),
    CANNOT_DELETE("Cannot delete file or dir.");

    @Getter
    private String statusText;

    DataMessageStatus(String statusText) {
        this.statusText = statusText;
    }


}