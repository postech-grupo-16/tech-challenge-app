package com.fiap.tech_challenge_backend.shared.domain.valueobjects;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ValorInvalidoException;

import java.util.Objects;

public class CpfCnpj {

    private static final String REGEX = "^(\\d{11}|[A-Za-z0-9]{14})$";

    private String valor;

    public CpfCnpj(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new ValorInvalidoException("CPF/CNPJ", valor);
        }

        String valorNormalizado = valor.trim().toUpperCase();

        if (!valorNormalizado.matches(REGEX)) {
            throw new ValorInvalidoException("CPF/CNPJ", valor);
        }

        this.valor = valorNormalizado;
    }

    public boolean isCpf() {
        return valor.matches("^\\d{11}$");
    }

    public boolean isCnpj() {
        return valor.matches("^[A-Z0-9]{14}$");
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

        if (!(object instanceof CpfCnpj cpfCnpj)) {
            return false;
        }

        return Objects.equals(valor, cpfCnpj.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}
