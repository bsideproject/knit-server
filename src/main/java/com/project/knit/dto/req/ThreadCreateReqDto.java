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
public class ThreadCreateReqDto {
    @NotBlank(message = "Thread Title is required.")
    private String title;
    @NotBlank(message = "Thread SubTitle is required.")
    private String subTitle;
    private String thumbnailUrl;
    private String summary;
    @NotNull(message = "At least one content is required.")
    private List<ContentReqDto> contents;
    @NotNull(message = "At least one tag is required.")
    private List<String> tags;
    @NotNull(message = "At least one category is required.")
    private List<CategoryReqDto> categories;
    private List<ReferenceReqDto> references;
}
