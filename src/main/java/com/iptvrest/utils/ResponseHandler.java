package com.iptvrest.utils;

import lombok.Data;

@Data
public class ApiResponse {

    private final String message;

    public ApiResponse(String message) {
        this.message = message;
    }
}
