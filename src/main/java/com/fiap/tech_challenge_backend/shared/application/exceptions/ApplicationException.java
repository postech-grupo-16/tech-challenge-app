package com.fiap.tech_challenge_backend.shared.application.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;
    private final List<String> details;

    protected ApplicationException(
            HttpStatus status,
            String errorCode,
            String message
    ) {
        this(status, errorCode, message, List.of());
    }

    protected ApplicationException(
        HttpStatus status,
        String errorCode,
        String message,
        List<String> details
    ) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.details = details;
    }

}
