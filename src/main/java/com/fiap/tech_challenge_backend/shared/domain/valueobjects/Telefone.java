package com.fiap.tech_challenge_backend.shared.domain.valueobjects;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ValorInvalidoException;

import java.util.Objects;

public class Telefone {

    private static final String REGEX = "^\\d{10,11}$";

    private String valor;

    public Telefone(String valor) {
        if (valor == null || valor.isBlank()) {
            this.valor = null;
            return;
        }

        String valorNormalizado = valor.trim();

        if (!valorNormalizado.matches(REGEX)) {
            throw new ValorInvalidoException("telefone", valor);
        }

        this.valor = valorNormalizado;
    }

    public String valor() {
        return valor;
    }

    public boolean informado() {
        return valor != null && !valor.isBlank();
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

        if (!(object instanceof Telefone telefone)) {
            return false;
        }

        return Objects.equals(valor, telefone.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}
