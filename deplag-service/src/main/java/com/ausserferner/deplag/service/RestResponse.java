package com.ausserferner.deplag.service;

import com.ausserferner.deplag.DeplagException;
import com.ausserferner.deplag.ErrorCode;

import java.util.HashMap;
import java.util.Map;

public class RestResponse extends HashMap<String,Object> {
    public RestResponse() {
        put("version", 0.1);
        put("timestamp", System.currentTimeMillis());
    }
    public RestResponse(Map<String,Object> map) {
        this();
//        put("operationTime", System.currentTimeMillis() - beginOfOperation);
        putAll(map);
    }

    public RestResponse(Exception exception) {
        this();
        if(exception instanceof DeplagException) {
            DeplagException de = (DeplagException)exception;
            put("error", de.getErrorCode());
            put("fields", de.getFields());
        } else {
            put("error", ErrorCode.SERVER_ERROR);
        }
        put("message", exception.getMessage());
    }
}
