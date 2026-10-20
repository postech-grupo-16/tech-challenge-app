package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public record OrcamentoDTO(
        Long id,
        TipoOrcamento tipo,
        StatusOrcamento status,
        BigDecimal valorTotal,
        LocalDateTime prazoEstipulado,
        LocalDateTime dataCriacao,
        List<ServicoDTO> servicos,
        List<PecaDTO> pecas
) {
    /**
     * @param nomePeca resolve o nome de uma peça/insumo a partir do seu ID — {@code OsPeca} não
     *                  navega mais o objeto {@code PecaInsumo} diretamente (contexto diferente).
     */
    public static OrcamentoDTO from(OsOrcamento orcamento, Function<UUID, String> nomePeca) {
        return new OrcamentoDTO(
                orcamento.getId(),
                orcamento.getTipo(),
                orcamento.getStatus(),
                orcamento.getValorTotal(),
                orcamento.getPrazoEstipulado(),
                orcamento.getDataCriacao(),
                orcamento.getServicos().stream()
                        .map(ServicoDTO::from)
                        .toList(),
                orcamento.getPecas().stream()
                        .map(p -> PecaDTO.from(p, nomePeca.apply(p.getPecaInsumoId())))
                        .toList()
        );
    }
}
