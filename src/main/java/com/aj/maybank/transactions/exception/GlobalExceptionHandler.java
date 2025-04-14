package com.aj.maybank.transactions.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle concurrent update conflicts (409)
    @ExceptionHandler(ConcurrentUpdateException.class)
    public ResponseEntity<String> handleConcurrentUpdate(ConcurrentUpdateException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

}