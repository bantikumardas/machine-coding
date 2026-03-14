package com.example.MachineCoding.ErrorHandler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private String error;
    private String path;
    private Timestamp timestamp;
}
