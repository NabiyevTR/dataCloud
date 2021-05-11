package ntr.datacloud.common.messages.data;

import lombok.Getter;

public enum DataMessageStatus {
    NO_RESPONSE("No response from server"),
    OK("Operation completed successfully"),
    UNKNOWN_ERROR("Unknown error"),
    CANNOT_GET_FILES_FROM_SERVER("Cannot get files from server"),
    NO_SUCH_DIRECTORY("No such directory"),
    ACCESS_DENIED("Access denied"),
    UNKNOWN_COMMAND("Unknown command"),
    CANNOT_DELETE("Cannot delete file or dir"),
    FILE_EXISTS("File exists"),
    DIRECTORY_EXISTS("Directory exists"),
    CANNOT_CREATE_DIR("Cannot create dir"),
    CANNOT_RENAME("Cannot rename file o directory"),
    CANNOT_UPLOAD("Cannot upload file");

    @Getter
    private String statusText;

    DataMessageStatus(String statusText) {
        this.statusText = statusText;
    }

}