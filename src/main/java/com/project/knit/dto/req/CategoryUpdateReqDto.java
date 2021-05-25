package com.project.knit.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@NoArgsConstructor
@Setter
@Getter
public class CategoryUpdateReqDto {
    private Long categoryId;
    @NotBlank(message = "Category Value is required.")
    private String value;
}
