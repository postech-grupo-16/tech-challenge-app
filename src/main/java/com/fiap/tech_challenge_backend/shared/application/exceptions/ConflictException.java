package com.fiap.tech_challenge_backend.shared.application.exceptions;

import org.springframework.http.HttpStatus;

public abstract class ConflictException extends ApplicationException {

    protected ConflictException(String errorCode, String message) {
        super(HttpStatus.CONFLICT, errorCode, message);
    }
}
