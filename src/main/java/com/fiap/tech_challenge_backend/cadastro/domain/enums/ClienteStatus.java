package com.fiap.tech_challenge_backend.cadastro.domain.enums;

/**
 * Status do cliente para fins de autenticação via CPF.
 * Apenas clientes {@code ATIVO} conseguem autenticar através da Function Serverless.
 */
public enum ClienteStatus {
    ATIVO,
    INATIVO
}
