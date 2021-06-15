package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ThreadRecentChangedResDto {
    private Long threadId;
    private String title;
    private LocalDateTime modifiedDate;
}
