package com.ausserferner.deplag;

public enum ErrorCode {
    PARAMETER_INVALID(100), DOCUMENT_ALREADY_INDEXED(101), SERVER_ERROR(1);

    private int code;

    ErrorCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}
