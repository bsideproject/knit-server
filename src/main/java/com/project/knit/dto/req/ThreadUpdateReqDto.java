package com.project.knit.dto.req;

import com.project.knit.domain.entity.Category;
import com.project.knit.domain.entity.Content;
import com.project.knit.domain.entity.ThreadReference;
import com.project.knit.domain.entity.Tag;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ThreadUpdateReqDto {
    private String subTitle;
    private String thumbnail;
    private String summary;
    private List<Content> contentList;
    private List<Tag> tagList;
    private List<Category> categoryList;
    private List<ThreadReference> threadReferenceList;
}
