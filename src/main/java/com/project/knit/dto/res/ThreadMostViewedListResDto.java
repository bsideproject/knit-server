package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ThreadMostViewedListResDto {
    private Integer count;
    private List<ThreadMostViewedResDto> mostViewedThreads;
}
