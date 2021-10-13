package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ThreadShortResDto {
    private Long id;
    private String title;
    private String subTitle;
    private String thumbnailUrl;
    private Integer likeCount;
}
