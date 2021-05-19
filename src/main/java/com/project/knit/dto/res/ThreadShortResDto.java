package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ThreadShortResDto {
    private Long threadId;
    private String threadTitle;
    private String threadSubTitle;
    private String thumbnailUrl;
}
