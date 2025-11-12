package org.example.accountservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorMessage {
    private String message;
    private HttpStatus status;

    public ErrorMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

}
