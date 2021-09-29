package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ThreadContributeResDto {
    private Long contributorUserId;
    private String year;
    private String month;
    private LocalDateTime createdDate;
    private String type;
    private Long threadId;
    private String threadTitle;
}
