package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;

import java.util.ArrayList;
import java.util.List;

public final class OrdemServicoMapper {

    private OrdemServicoMapper() {
    }

    public static OrdemServico toDomain(OrdemServicoJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        OrdemServico domain = OrdemServico.builder()
                .id(entity.getId())
                .clienteId(entity.getClienteId())
                .veiculoId(entity.getVeiculoId())
                .mecanicoId(entity.getMecanicoId())
                .status(entity.getStatus())
                .valorTotalAcumulado(entity.getValorTotalAcumulado())
                .valorTotal(entity.getValorTotal())
                .dataCriacao(entity.getDataCriacao())
                .dataInicioExecucao(entity.getDataInicioExecucao())
                .dataFinalizacao(entity.getDataFinalizacao())
                .urgente(entity.getUrgente())
                .queixaCliente(entity.getQueixaCliente())
                .observacoes(entity.getObservacoes())
                .build();

        List<com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento> orcamentos = new ArrayList<>();
        if (entity.getOrcamentos() != null) {
            for (OsOrcamentoJpaEntity orcEntity : entity.getOrcamentos()) {
                orcamentos.add(OsOrcamentoMapper.toDomain(orcEntity, domain));
            }
        }
        domain.setOrcamentos(orcamentos);

        List<com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus> historico = new ArrayList<>();
        if (entity.getHistoricoStatus() != null) {
            for (OsHistoricoStatusJpaEntity histEntity : entity.getHistoricoStatus()) {
                historico.add(OsHistoricoStatusMapper.toDomain(histEntity, domain));
            }
        }
        domain.setHistoricoStatus(historico);

        return domain;
    }

    public static OrdemServicoJpaEntity toEntity(OrdemServico domain) {
        if (domain == null) {
            return null;
        }

        OrdemServicoJpaEntity entity = OrdemServicoJpaEntity.builder()
                .id(domain.getId())
                .clienteId(domain.getClienteId())
                .veiculoId(domain.getVeiculoId())
                .mecanicoId(domain.getMecanicoId())
                .status(domain.getStatus())
                .valorTotalAcumulado(domain.getValorTotalAcumulado())
                .valorTotal(domain.getValorTotal())
                .dataCriacao(domain.getDataCriacao())
                .dataInicioExecucao(domain.getDataInicioExecucao())
                .dataFinalizacao(domain.getDataFinalizacao())
                .urgente(domain.getUrgente())
                .queixaCliente(domain.getQueixaCliente())
                .observacoes(domain.getObservacoes())
                .build();

        List<OsOrcamentoJpaEntity> orcamentos = new ArrayList<>();
        if (domain.getOrcamentos() != null) {
            for (var orc : domain.getOrcamentos()) {
                OsOrcamentoJpaEntity orcEntity = OsOrcamentoMapper.toEntity(orc);
                orcEntity.setOrdemServico(entity);
                orcamentos.add(orcEntity);
            }
        }
        entity.setOrcamentos(orcamentos);

        List<OsHistoricoStatusJpaEntity> historico = new ArrayList<>();
        if (domain.getHistoricoStatus() != null) {
            for (var hist : domain.getHistoricoStatus()) {
                OsHistoricoStatusJpaEntity histEntity = OsHistoricoStatusMapper.toEntity(hist);
                histEntity.setOrdemServico(entity);
                historico.add(histEntity);
            }
        }
        entity.setHistoricoStatus(historico);

        return entity;
    }
}
