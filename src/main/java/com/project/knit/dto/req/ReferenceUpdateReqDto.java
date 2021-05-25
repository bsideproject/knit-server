package com.project.knit.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Setter
@Getter
public class ReferenceUpdateReqDto {
    private Long referenceId;
    @NotBlank(message = "Reference Link is required.")
    private String referenceLink;
    private String referenceDescription;
}
