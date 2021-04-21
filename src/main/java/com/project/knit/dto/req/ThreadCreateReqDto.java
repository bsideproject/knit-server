package com.project.knit.dto.req;

import com.project.knit.domain.entity.Category;
import com.project.knit.domain.entity.Content;
import com.project.knit.domain.entity.Reference;
import com.project.knit.domain.entity.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ThreadCreateReqDto {
    private String title;
    private String subTitle;
    private String thumbnail;
    private String summary;
    private List<Content> contentList;
    private List<Tag> tagList;
    private List<Category> categoryList;
    private List<Reference> referenceList;
}
