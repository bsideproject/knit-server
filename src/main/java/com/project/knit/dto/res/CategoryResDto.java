package com.project.knit.dto.res;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.knit.domain.entity.Thread;
import com.project.knit.domain.entity.TimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
public class CategoryResDto {
    private Long categoryId;
    private String category;
}
