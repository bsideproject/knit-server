package com.project.knit.utils.enums;

public enum CategoryType {
    PLANNING("기획", "PLANNING"),
    DESIGN("디자인", "DESIGN"),
    DEVELOP("개발", "DEVELOP"),
    MARKETING("마케팅", "MARKETING"),
    DATA("데이터분석", "DATA");

    private final String groupKr;
    private final String groupEn;

    CategoryType(String groupKr, String groupEn) {
        this.groupKr = groupKr;
        this.groupEn = groupEn;
    }

    public String getGroupKr() {
        return groupKr;
    }

    public String getGroupEn() {
        return groupEn;
    }
}
