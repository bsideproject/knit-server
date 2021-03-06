package com.project.knit.dto.req;

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
    private Long id;
    private String title;
    @NotBlank(message = "Thread SubTitle is required.")
    private String subTitle;
    private String thumbnailUrl;
    private String summary;
    @NotNull(message = "At least one content is required.")
    private List<ContentUpdateReqDto> contents;
    @NotNull(message = "At least one tag is required.")
    private List<TagUpdateReqDto> tags;
    @NotNull(message = "At least one category is required.")
    private List<CategoryUpdateReqDto> categories;
    private List<ReferenceUpdateReqDto> references;
}
