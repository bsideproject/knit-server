package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ThreadAdminResDto {
    private Long threadId;
    private String nickname;
    private String threadTitle;
    private String threadSubTitle;
    private String threadThumbnail;
    private List<ContentResDto> contentList;
    private List<TagResDto> tagList;
    private List<CategoryResDto> categoryList;
    private List<ReferenceResDto> referenceList;
    private String status;
    private LocalDateTime createdDate;
}
