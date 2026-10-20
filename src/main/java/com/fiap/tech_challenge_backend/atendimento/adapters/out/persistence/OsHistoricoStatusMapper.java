package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;

public final class OsHistoricoStatusMapper {

    private OsHistoricoStatusMapper() {
    }

    public static OsHistoricoStatus toDomain(OsHistoricoStatusJpaEntity entity, OrdemServico ordemServico) {
        if (entity == null) {
            return null;
        }
        return OsHistoricoStatus.builder()
                .id(entity.getId())
                .ordemServico(ordemServico)
                .statusOrigem(entity.getStatusOrigem())
                .statusDestino(entity.getStatusDestino())
                .dataMudanca(entity.getDataMudanca())
                .usuarioId(entity.getUsuarioId())
                .build();
    }

    public static OsHistoricoStatusJpaEntity toEntity(OsHistoricoStatus domain) {
        if (domain == null) {
            return null;
        }
        return OsHistoricoStatusJpaEntity.builder()
                .id(domain.getId())
                .statusOrigem(domain.getStatusOrigem())
                .statusDestino(domain.getStatusDestino())
                .dataMudanca(domain.getDataMudanca())
                .usuarioId(domain.getUsuarioId())
                .build();
    }
}
