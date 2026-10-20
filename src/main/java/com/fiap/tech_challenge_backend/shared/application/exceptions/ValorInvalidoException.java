package com.fiap.tech_challenge_backend.shared.application.exceptions;

public class ValorInvalidoException extends BusinessRuleException {

    public ValorInvalidoException(String campo, String valor) {
        super(
                "VALOR_INVALIDO",
                "Valor inválido para o campo " + campo + ": " + valor
        );
    }
}
