package org.example.accountservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ControllerException.class)
    public ResponseEntity<ErrorMessage> handleException(ControllerException exception){
        log.error(exception.getMessage(),exception);
        return ResponseEntity.status(exception.getHttpStatus()).body(new ErrorMessage(exception.getMessage(),exception.getHttpStatus()));
    }
}
