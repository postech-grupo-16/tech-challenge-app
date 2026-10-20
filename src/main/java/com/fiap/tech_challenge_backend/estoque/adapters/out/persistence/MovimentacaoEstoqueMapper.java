package com.fiap.tech_challenge_backend.estoque.adapters.out.persistence;

import com.fiap.tech_challenge_backend.estoque.domain.entities.MovimentacaoEstoque;

public final class MovimentacaoEstoqueMapper {

    private MovimentacaoEstoqueMapper() {
    }

    public static MovimentacaoEstoque toDomain(MovimentacaoEstoqueJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return MovimentacaoEstoque.builder()
                .id(entity.getId())
                .pecaInsumo(PecaInsumoMapper.toDomain(entity.getPecaInsumo()))
                .tipoMovimentacao(entity.getTipoMovimentacao())
                .quantidade(entity.getQuantidade())
                .observacao(entity.getObservacao())
                .criadoEm(entity.getCriadoEm())
                .build();
    }

    public static MovimentacaoEstoqueJpaEntity toEntity(MovimentacaoEstoque domain) {
        if (domain == null) {
            return null;
        }
        return MovimentacaoEstoqueJpaEntity.builder()
                .id(domain.getId())
                .pecaInsumo(PecaInsumoMapper.toEntity(domain.getPecaInsumo()))
                .tipoMovimentacao(domain.getTipoMovimentacao())
                .quantidade(domain.getQuantidade())
                .observacao(domain.getObservacao())
                .criadoEm(domain.getCriadoEm())
                .build();
    }
}
