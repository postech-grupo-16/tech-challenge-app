package com.fiap.tech_challenge_backend.acesso.adapters.out.persistence;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;

public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static Usuario toDomain(UsuarioJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Usuario.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail() != null ? new Email(entity.getEmail()) : null)
                .senha(entity.getSenha())
                .telefone(entity.getTelefone() != null ? new Telefone(entity.getTelefone()) : null)
                .perfil(entity.getPerfil())
                .cpfCnpj(entity.getCpfCnpj() != null ? new CpfCnpj(entity.getCpfCnpj()) : null)
                .build();
    }

    public static UsuarioJpaEntity toEntity(Usuario domain) {
        if (domain == null) {
            return null;
        }
        return UsuarioJpaEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .email(domain.getEmail() != null ? domain.getEmail().valor() : null)
                .senha(domain.getSenha())
                .telefone(domain.getTelefone() != null ? domain.getTelefone().valor() : null)
                .perfil(domain.getPerfil())
                .cpfCnpj(domain.getCpfCnpj() != null ? domain.getCpfCnpj().valor() : null)
                .build();
    }
}
