package com.fiap.tech_challenge_backend.shared.application.exceptions;

import org.springframework.http.HttpStatus;

public abstract class BusinessRuleException extends ApplicationException {

    protected BusinessRuleException(String errorCode, String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, errorCode, message);
    }
}
