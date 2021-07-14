package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ProfileResDto {
    private String email;
    private String nickname;
    private String github;
    private String linkedIn;
    private String introduction;
}
