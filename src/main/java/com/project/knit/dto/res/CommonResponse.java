package com.project.knit.dto.res;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@NoArgsConstructor
public class CommonResponse {
    private String message;
}
