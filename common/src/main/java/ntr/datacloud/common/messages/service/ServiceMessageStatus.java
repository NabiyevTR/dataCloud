package ntr.datacloud.common.messages.service;

import lombok.Getter;

public enum ServiceMessageStatus {
    NO_RESPONSE("No response from server.") ,
    OK ("Operation completed successfully."),
    INCORRECT_PASSWORD_OR_LOGIN("Incorrect login or password."),
    LOGIN_IS_NOT_AVAILABLE ("Login is taken by another user."),
    USER_IS_ALREADY_AUTHENTIFICATED("User is already authentificated"),
    UNKNOWN_ERROR("Unknown error.");

    @Getter
    private String statusText;

    ServiceMessageStatus(String statusText) {
        this.statusText = statusText;
    }


}