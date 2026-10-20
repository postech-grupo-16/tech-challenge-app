package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;

import java.util.UUID;

public record ClienteInfoDTO(
        UUID id,
        String nome,
        String telefone,
        String email
) {
    /**
     * @param email e-mail do usuário associado ao cliente, já resolvido pelo chamador via
     *              {@code acesso.UsuarioRepository} a partir de {@code cliente.getUsuarioId()} —
     *              este DTO não tem acesso a portas para buscar isso sozinho.
     */
    public static ClienteInfoDTO from(Cliente cliente, String email) {
        return new ClienteInfoDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone() != null ? cliente.getTelefone().valor() : null,
                email
        );
    }
}
