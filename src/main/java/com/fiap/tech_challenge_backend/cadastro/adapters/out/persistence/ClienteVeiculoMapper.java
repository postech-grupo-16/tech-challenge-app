package com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.ClienteVeiculo;

public final class ClienteVeiculoMapper {

    private ClienteVeiculoMapper() {
    }

    public static ClienteVeiculo toDomain(ClienteVeiculoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ClienteVeiculo.builder()
                .clienteId(entity.getClienteId())
                .veiculoId(entity.getVeiculoId())
                .ativo(entity.getAtivo())
                .build();
    }

    public static ClienteVeiculoJpaEntity toEntity(ClienteVeiculo domain) {
        if (domain == null) {
            return null;
        }
        return ClienteVeiculoJpaEntity.builder()
                .clienteId(domain.getClienteId())
                .veiculoId(domain.getVeiculoId())
                .ativo(domain.getAtivo())
                .build();
    }
}
