package com.fiap.tech_challenge_backend.acesso.application.ports.in;

/**
 * Porta de entrada para o caso de uso de autenticação.
 * Contexto Delimitado: acesso
 */
public interface AutenticarUsuarioUseCase {

    /**
     * Autentica um usuário por e-mail e senha e retorna um token JWT.
     *
     * @throws com.fiap.tech_challenge_backend.acesso.application.exceptions.CredenciaisInvalidasException
     *         se o e-mail não existir ou a senha não conferir
     */
    String autenticar(String email, String senha);
}
