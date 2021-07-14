package com.project.knit.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ProfileUpdateReqDto {
    private String nickname;
    private String github;
    private String linkedIn;
    private String introduction;
}
