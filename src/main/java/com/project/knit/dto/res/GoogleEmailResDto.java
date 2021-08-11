package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoogleEmailResDto {
    private String sub;
    private String picture;
    private String email;
    private boolean email_verified;
}
