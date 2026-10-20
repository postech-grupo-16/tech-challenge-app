package com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;

public final class VeiculoMapper {

    private VeiculoMapper() {
    }

    public static Veiculo toDomain(VeiculoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Veiculo.builder()
                .id(entity.getId())
                .placa(entity.getPlaca() != null ? new Placa(entity.getPlaca()) : null)
                .marca(entity.getMarca())
                .modelo(entity.getModelo())
                .ano(entity.getAno())
                .cor(entity.getCor())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public static VeiculoJpaEntity toEntity(Veiculo domain) {
        if (domain == null) {
            return null;
        }
        return VeiculoJpaEntity.builder()
                .id(domain.getId())
                .placa(domain.getPlaca() != null ? domain.getPlaca().valor() : null)
                .marca(domain.getMarca())
                .modelo(domain.getModelo())
                .ano(domain.getAno())
                .cor(domain.getCor())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}
