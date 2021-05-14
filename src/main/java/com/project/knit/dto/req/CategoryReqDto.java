package com.project.knit.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Setter
@Getter
public class CategoryReqDto {
    @NotBlank(message = "Category is required.")
    private String category;
    private String description;
}
