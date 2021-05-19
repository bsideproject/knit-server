package com.project.knit.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@NoArgsConstructor
@Setter
@Getter
public class TagReqDto {
    @NotBlank(message = "Tag Value is required.")
    private String value;
}
