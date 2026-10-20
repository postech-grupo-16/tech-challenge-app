package com.fiap.tech_challenge_backend.estoque.adapters.out.persistence;

import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;

public final class PecaInsumoMapper {

    private PecaInsumoMapper() {
    }

    public static PecaInsumo toDomain(PecaInsumoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return PecaInsumo.builder()
                .id(entity.getId())
                .tipo(entity.getTipo())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .precoVenda(entity.getPrecoVenda())
                .precoCompra(entity.getPrecoCompra())
                .quantidadePorUnidade(entity.getQuantidadePorUnidade())
                .quantidadeEstoque(entity.getQuantidadeEstoque())
                .quantidadeMinima(entity.getQuantidadeMinima())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public static PecaInsumoJpaEntity toEntity(PecaInsumo domain) {
        if (domain == null) {
            return null;
        }
        return PecaInsumoJpaEntity.builder()
                .id(domain.getId())
                .tipo(domain.getTipo())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .precoVenda(domain.getPrecoVenda())
                .precoCompra(domain.getPrecoCompra())
                .quantidadePorUnidade(domain.getQuantidadePorUnidade())
                .quantidadeEstoque(domain.getQuantidadeEstoque())
                .quantidadeMinima(domain.getQuantidadeMinima())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}
