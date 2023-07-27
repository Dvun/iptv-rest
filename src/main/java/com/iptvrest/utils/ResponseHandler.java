package com.iptvrest.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {

    public static ResponseEntity<Object> response(String message, HttpStatus httpStatus, Object responseObj) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("status", httpStatus.value());
        map.put("data", responseObj);

        return new ResponseEntity<>(map, httpStatus);
    }

}
