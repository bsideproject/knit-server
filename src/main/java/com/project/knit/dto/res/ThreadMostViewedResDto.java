package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ThreadMostViewedResDto {
    private Long threadId;
    private String title;
    private String contentSummary;
    private Long viewCount;
    private Long likeCount;
}
