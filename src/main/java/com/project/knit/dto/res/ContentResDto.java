package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ContentResDto {
    private Long contentId;
    private String type;
    private String value;
    private String summary;
}
