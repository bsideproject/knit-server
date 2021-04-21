package com.project.knit.utils.enums;

public enum ResponseMessageEnum {

    SUCCESS("Success"),
    FAIL("Fail");

    private final String message;

    ResponseMessageEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
