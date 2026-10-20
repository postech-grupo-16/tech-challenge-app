package com.fiap.tech_challenge_backend.shared.domain.valueobjects;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ValorInvalidoException;

import java.util.Objects;
import java.util.regex.Pattern;

public class Email {

    private static final Pattern REGEX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );

    private String valor;

    public Email(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new ValorInvalidoException("email", valor);
        }

        String valorNormalizado = valor.trim().toLowerCase();

        if (!REGEX.matcher(valorNormalizado).matches()) {
            throw new ValorInvalidoException("email", valor);
        }

        this.valor = valorNormalizado;
    }

    public String valor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Email email)) {
            return false;
        }

        return Objects.equals(valor, email.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}
