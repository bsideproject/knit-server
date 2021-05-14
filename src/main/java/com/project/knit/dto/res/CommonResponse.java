package com.project.knit.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class CommonResponse<T> {
    private int status;  // HttpStatus 코드 Enum 관리
    private String message; // ResponseMessage Enum 관리
    private T data;

    public CommonResponse(final int status, final String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public static <T> CommonResponse<T> response(final int status, final String message) {
        return response(status, message, null);
    }

    public static <T> CommonResponse<T> response(final int status, final String message, final T data) {
        return CommonResponse.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
    }
}
