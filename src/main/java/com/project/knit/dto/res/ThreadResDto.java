package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ThreadResDto {
    private Long threadId;
    private String threadTitle;
    private String threadSubTitle;
    private String threadThumbnail;
    private List<ContentResDto> contentList;
    private List<TagResDto> tagList;
    private List<CategoryResDto> categoryList;
    private List<ReferenceResDto> referenceList;
}
