package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MecanicoInfoDTO(
        UUID id,
        String nome,
        String email,
        String telefone
) {
    public static MecanicoInfoDTO from(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new MecanicoInfoDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail() != null ? usuario.getEmail().toString() : null,
                usuario.getTelefone() != null ? usuario.getTelefone().toString() : null
        );
    }
}
