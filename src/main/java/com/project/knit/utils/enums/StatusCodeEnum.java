package com.project.knit.utils.enums;

public enum StatusCodeEnum {
    OK(200),
    CREATED(201),
    NO_CONTENT(204),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    CONFLICT(409),
    INTERNAL_SERVER_ERROR(500),
    SERVICE_UNAVAILABLE(503),
    DB_ERROR(600);

    private int status;

    StatusCodeEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
