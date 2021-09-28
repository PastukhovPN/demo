package com.example.demo.controllers.models;

import lombok.Data;

@Data
public class ExceptionResponse {
    private final String massage;

    public ExceptionResponse(String massage) {
        this.massage = massage;
    }
}
