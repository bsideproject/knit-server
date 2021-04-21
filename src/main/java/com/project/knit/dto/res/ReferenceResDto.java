package com.project.knit.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ReferenceResDto {
    private Long referenceId;
    private String referenceLink;
    private String referenceDescription;
}
