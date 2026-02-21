package com.sdcote.sdp;

public enum SearchCondition {
    IS("is"),
    IS_NOT("is not"),
    CONTAINS("contains"),
    NOT_CONTAINS("not contains"),
    STARTS_WITH("starts with"),
    ENDS_WITH("ends with"),
    GREATER_THAN("greater than"),
    GREATER_OR_EQUAL("greater or equal"),
    LESSER_THAN("lesser than"),
    LESSER_OR_EQUAL("lesser or equal"),
    BETWEEN("between"),
    NOT_BETWEEN("not between");

    private final String value;

    SearchCondition(String value) {
        this.value = value;
    }

    /**
     *
     */
    public String getValue() {
        return value;
    }
}