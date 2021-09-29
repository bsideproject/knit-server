package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ThreadPagingResDto {
    private Integer total;
    private Integer nextPage;
    private List<ThreadResDto> threads;
}
