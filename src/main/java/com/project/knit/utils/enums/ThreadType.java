package com.project.knit.utils.enums;

public enum ThreadType {
    THREAD("문서"),
    QUESTION("질문"),
    DISCUSSION("토론");

    private final String type;

    ThreadType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
