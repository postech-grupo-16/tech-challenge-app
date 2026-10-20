package com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Cep;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;

public final class ClienteMapper {

    private ClienteMapper() {
    }

    public static Cliente toDomain(ClienteJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Cliente.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId())
                .nome(entity.getNome())
                .cpfCnpj(entity.getCpfCnpj() != null ? new CpfCnpj(entity.getCpfCnpj()) : null)
                .telefone(entity.getTelefone() != null ? new Telefone(entity.getTelefone()) : null)
                .cep(entity.getCep() != null ? new Cep(entity.getCep()) : null)
                .rua(entity.getRua())
                .numero(entity.getNumero())
                .complemento(entity.getComplemento())
                .cidade(entity.getCidade())
                .estado(entity.getEstado())
                .status(entity.getStatus())
                .build();
    }

    public static ClienteJpaEntity toEntity(Cliente domain) {
        if (domain == null) {
            return null;
        }
        return ClienteJpaEntity.builder()
                .id(domain.getId())
                .usuarioId(domain.getUsuarioId())
                .nome(domain.getNome())
                .cpfCnpj(domain.getCpfCnpj() != null ? domain.getCpfCnpj().valor() : null)
                .telefone(domain.getTelefone() != null ? domain.getTelefone().valor() : null)
                .cep(domain.getCep() != null ? domain.getCep().valor() : null)
                .rua(domain.getRua())
                .numero(domain.getNumero())
                .complemento(domain.getComplemento())
                .cidade(domain.getCidade())
                .estado(domain.getEstado())
                .status(domain.getStatus())
                .build();
    }
}
