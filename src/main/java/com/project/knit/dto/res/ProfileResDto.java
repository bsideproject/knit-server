package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class ProfileResDto {
    private String email;
    private String nickname;
    private String github;
    private String linkedIn;
    private String introduction;
    private List<ThreadContributeResDto> contributeHistoryList;
}
