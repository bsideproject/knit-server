package com.project.knit.dto.req;

import com.project.knit.domain.entity.Category;
import com.project.knit.domain.entity.Content;
import com.project.knit.domain.entity.Reference;
import com.project.knit.domain.entity.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ThreadUpdateReqDto {
    @NotBlank(message = "Thread SubTitle is required.")
    private String subTitle;
    private String thumbnailUrl;
    private String summary;
    @NotNull(message = "At least one content is required.")
    private List<Content> contents;
    @NotNull(message = "At least one tag is required.")
    private List<TagReqDto> tags;
    @NotNull(message = "At least one category is required.")
    private List<Category> categories;
    private List<Reference> references;
}
