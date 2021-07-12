package com.project.knit.utils.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST", "게스트"),
    ADMIN("ROLE_ADMIN", "어드민"),
    USER("ROLE_USER", "사용자");
    private final String key;
    private final String title;
}
