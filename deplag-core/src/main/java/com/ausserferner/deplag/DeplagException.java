package com.ausserferner.deplag;

public class DeplagException extends Exception {

    private ErrorCode code;
    private String[] fields;
    
    public DeplagException(ErrorCode code, String message, String... fields) {
        super(message);        
    }
    
    public ErrorCode getErrorCode() {
        return code;
    }
    
    public String[] getFields() {
        return fields;
    }
}
