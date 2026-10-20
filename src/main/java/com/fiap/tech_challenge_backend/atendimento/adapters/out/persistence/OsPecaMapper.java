package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsPeca;

public final class OsPecaMapper {

    private OsPecaMapper() {
    }

    public static OsPeca toDomain(OsPecaJpaEntity entity, OsOrcamento orcamento) {
        if (entity == null) {
            return null;
        }
        return OsPeca.builder()
                .id(entity.getId())
                .orcamento(orcamento)
                .ordemServicoId(entity.getOrdemServicoId())
                .pecaInsumoId(entity.getPecaInsumoId())
                .quantidade(entity.getQuantidade())
                .precoVendaAplicado(entity.getPrecoVendaAplicado())
                .build();
    }

    public static OsPecaJpaEntity toEntity(OsPeca domain) {
        if (domain == null) {
            return null;
        }
        return OsPecaJpaEntity.builder()
                .id(domain.getId())
                .ordemServicoId(domain.getOrdemServicoId())
                .pecaInsumoId(domain.getPecaInsumoId())
                .quantidade(domain.getQuantidade())
                .precoVendaAplicado(domain.getPrecoVendaAplicado())
                .build();
    }
}
