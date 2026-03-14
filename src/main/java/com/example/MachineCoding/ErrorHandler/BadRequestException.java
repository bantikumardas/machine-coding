package com.example.MachineCoding.ErrorHandler;

public class BadRequestException extends  RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
