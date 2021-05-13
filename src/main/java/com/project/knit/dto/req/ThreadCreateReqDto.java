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
public class ThreadCreateReqDto {
    @NotBlank
    private String title;
    @NotBlank
    private String subTitle;
    @NotBlank
    private String thumbnailUrl;
    private String summary;
    @NotNull
    private List<Content> contents;
    @NotNull
    private List<Tag> tags;
    @NotNull
    private List<Category> categories;
    private List<Reference> references;
}
