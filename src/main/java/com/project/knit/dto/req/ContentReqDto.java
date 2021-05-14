package com.project.knit.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Setter
@Getter
public class ContentReqDto {
    @NotBlank(message = "Content Type is required.")
    private String contentType;
    @NotBlank(message = "Content Value is required.")
    private String value;
    private String summary;
}
