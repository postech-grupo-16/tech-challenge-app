package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsServico;

public final class OsServicoMapper {

    private OsServicoMapper() {
    }

    public static OsServico toDomain(OsServicoJpaEntity entity, OsOrcamento orcamento) {
        if (entity == null) {
            return null;
        }
        return OsServico.builder()
                .id(entity.getId())
                .orcamento(orcamento)
                .ordemServicoId(entity.getOrdemServicoId())
                .servico(ServicoCatalogoMapper.toDomain(entity.getServico()))
                .precoMaoDeObraAplicado(entity.getPrecoMaoDeObraAplicado())
                .build();
    }

    public static OsServicoJpaEntity toEntity(OsServico domain) {
        if (domain == null) {
            return null;
        }
        return OsServicoJpaEntity.builder()
                .id(domain.getId())
                .ordemServicoId(domain.getOrdemServicoId())
                .servico(ServicoCatalogoMapper.toEntity(domain.getServico()))
                .precoMaoDeObraAplicado(domain.getPrecoMaoDeObraAplicado())
                .build();
    }
}
