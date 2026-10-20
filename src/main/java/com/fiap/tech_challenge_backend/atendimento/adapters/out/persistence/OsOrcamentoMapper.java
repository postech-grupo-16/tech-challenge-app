package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsPeca;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsServico;

import java.util.ArrayList;
import java.util.List;

public final class OsOrcamentoMapper {

    private OsOrcamentoMapper() {
    }

    public static OsOrcamento toDomain(OsOrcamentoJpaEntity entity, OrdemServico ordemServico) {
        if (entity == null) {
            return null;
        }

        OsOrcamento domain = OsOrcamento.builder()
                .id(entity.getId())
                .ordemServico(ordemServico)
                .tipo(entity.getTipo())
                .status(entity.getStatus())
                .valorTotal(entity.getValorTotal())
                .prazoEstipulado(entity.getPrazoEstipulado())
                .dataCriacao(entity.getDataCriacao())
                .build();

        List<OsServico> servicos = new ArrayList<>();
        if (entity.getServicos() != null) {
            for (OsServicoJpaEntity s : entity.getServicos()) {
                servicos.add(OsServicoMapper.toDomain(s, domain));
            }
        }
        domain.setServicos(servicos);

        List<OsPeca> pecas = new ArrayList<>();
        if (entity.getPecas() != null) {
            for (OsPecaJpaEntity p : entity.getPecas()) {
                pecas.add(OsPecaMapper.toDomain(p, domain));
            }
        }
        domain.setPecas(pecas);

        return domain;
    }

    public static OsOrcamentoJpaEntity toEntity(OsOrcamento domain) {
        if (domain == null) {
            return null;
        }

        OsOrcamentoJpaEntity entity = OsOrcamentoJpaEntity.builder()
                .id(domain.getId())
                .tipo(domain.getTipo())
                .status(domain.getStatus())
                .valorTotal(domain.getValorTotal())
                .prazoEstipulado(domain.getPrazoEstipulado())
                .dataCriacao(domain.getDataCriacao())
                .build();

        List<OsServicoJpaEntity> servicos = new ArrayList<>();
        if (domain.getServicos() != null) {
            for (OsServico s : domain.getServicos()) {
                OsServicoJpaEntity sEntity = OsServicoMapper.toEntity(s);
                sEntity.setOrcamento(entity);
                servicos.add(sEntity);
            }
        }
        entity.setServicos(servicos);

        List<OsPecaJpaEntity> pecas = new ArrayList<>();
        if (domain.getPecas() != null) {
            for (OsPeca p : domain.getPecas()) {
                OsPecaJpaEntity pEntity = OsPecaMapper.toEntity(p);
                pEntity.setOrcamento(entity);
                pecas.add(pEntity);
            }
        }
        entity.setPecas(pecas);

        return entity;
    }
}
