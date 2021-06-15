package com.project.knit.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
public class ThreadLikeReqDto {
    @NotNull
    private Long threadId;
    @NotNull
    private Long userId;
}
