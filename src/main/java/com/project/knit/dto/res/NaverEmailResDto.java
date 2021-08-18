package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NaverEmailResDto {
    private String resultcode;
    private String message;
    private UserResDto response;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserResDto {
        private String id;
        private String email;
        private String name;
    }
}