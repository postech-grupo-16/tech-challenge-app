package com.fiap.tech_challenge_backend.shared.domain.valueobjects;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ValorInvalidoException;

import java.util.Objects;

public class Placa {

    private static final String REGEX = "^[A-Z]{3}([0-9]{4}|[0-9][A-Z][0-9]{2})$";

    private String valor;

    public Placa(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new ValorInvalidoException("placa", valor);
        }

        String valorNormalizado = valor.trim().toUpperCase();

        if (!valorNormalizado.matches(REGEX)) {
            throw new ValorInvalidoException("placa", valor);
        }

        this.valor = valorNormalizado;
    }

    public boolean isFormatoMercosul() {
        return valor.matches("^[A-Z]{3}[0-9][A-Z][0-9]{2}$");
    }

    public boolean isFormatoAntigo() {
        return valor.matches("^[A-Z]{3}[0-9]{4}$");
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
        if (this == object) return true;
        if (!(object instanceof Placa placa)) return false;
        return Objects.equals(valor, placa.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}
