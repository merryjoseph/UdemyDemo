package com.example.PasswordEmailSecurityDemo.ModelResponse;

public enum ErrorMessages {
    MISSING_REQUIRED_FIELD("missing required field.Please debug."),
    RECORD_ALREADY_EXISTS("record already exists.Please debug."),
    INTERNAL_SERVER_ERROR("internal error.Please debug."),
    NO_RECORD_FOUND("no record found.Please debug."),
    AUTHENTICATION_FAILED("authentication failed.Please debug."),
    COULD_NOT_UPDATE_RECORD("could not update record.Please debug."),
    COULD_NOT_DELETE_RECORD("could not delete record.Please debug."),
    EMAIL_NOT_VERIFIED("email could not be verified.Please debug.");

    private String errorMessage;

    ErrorMessages(String errorMessage){
        this.errorMessage=errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
