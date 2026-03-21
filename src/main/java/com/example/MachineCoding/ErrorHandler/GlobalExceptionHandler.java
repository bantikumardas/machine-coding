package com.example.MachineCoding.ErrorHandler;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static String stackTraceToString(Throwable ex, int maxFrames) {
        return Stream.of(ex.getStackTrace())
                .limit(maxFrames)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }

    private static String fullStackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String s = sw.toString();
        return s.length() > 4000 ? s.substring(0, 4000) + "\n..." : s;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException exception,
                                                        HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                404,
                exception.getMessage(),
                stackTraceToString(exception, 15),
                request.getRequestURI(),
                new Timestamp(System.currentTimeMillis())
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {
        String supported = ex.getSupportedMethods() != null
                ? String.join(", ", ex.getSupportedMethods())
                : "none";
        String msg = "HTTP " + ex.getMethod() + " is not supported for this URL. Supported methods: " + supported;
        ErrorResponse error = new ErrorResponse(
                405,
                msg,
                stackTraceToString(ex, 8),
                request.getRequestURI(),
                new Timestamp(System.currentTimeMillis())
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException exception,
                                                        HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                400,
                exception.getMessage(),
                stackTraceToString(exception, 15),
                request.getRequestURI(),
                new Timestamp(System.currentTimeMillis())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Handles @Valid / @Validated bean validation failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        // Collect all field errors into one message
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = new ErrorResponse(
                400,
                message,
                stackTraceToString(ex, 15),
                request.getRequestURI(),
                new Timestamp(System.currentTimeMillis())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unhandled exception on {}", request.getRequestURI(), ex);

        String errorDetail = ex.getClass().getName()
                + (ex.getMessage() != null && !ex.getMessage().isBlank() ? ": " + ex.getMessage() : "");

        ErrorResponse error = new ErrorResponse(
                500,
                "Something went wrong. Please try again later.",
                errorDetail + "\n" + fullStackTrace(ex),
                request.getRequestURI(),
                new Timestamp(System.currentTimeMillis())
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


}
