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
    private List<ContentResDto> contents;
    private List<TagResDto> tags;
    private List<CategoryResDto> categories;
    private List<ReferenceResDto> references;
    private String status;
    private LocalDateTime createdDate;
}
