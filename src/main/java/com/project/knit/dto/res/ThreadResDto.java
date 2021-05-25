package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ThreadResDto {
    private Long id;
    private String title;
    private String subTitle;
    private String thumbnailUrl;
    private List<ContentResDto> contents;
    private List<TagResDto> tags;
    private List<CategoryResDto> categories;
    private List<ReferenceResDto> references;
}
