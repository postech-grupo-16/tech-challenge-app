package com.fiap.tech_challenge_backend.acesso.application.exceptions;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ApplicationException;
import org.springframework.http.HttpStatus;

public class CredenciaisInvalidasException extends ApplicationException {
    public CredenciaisInvalidasException() {
        super(HttpStatus.UNAUTHORIZED, "CREDENCIAIS_INVALIDAS", "E-mail ou senha inválidos");
    }
}
