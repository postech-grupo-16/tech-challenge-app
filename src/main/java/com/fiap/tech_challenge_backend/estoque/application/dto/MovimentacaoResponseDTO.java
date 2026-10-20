package com.fiap.tech_challenge_backend.estoque.application.dto;

import com.fiap.tech_challenge_backend.estoque.domain.entities.MovimentacaoEstoque;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record MovimentacaoResponseDTO(
        UUID id,
        UUID pecaInsumoId,
        String pecaNome,
        TipoMovimentacao tipoMovimentacao,
        Integer quantidade,
        String observacao,
        LocalDateTime criadoEm
) {
    public static MovimentacaoResponseDTO from(MovimentacaoEstoque m) {
        return new MovimentacaoResponseDTO(
                m.getId(),
                m.getPecaInsumo().getId(),
                m.getPecaInsumo().getNome(),
                m.getTipoMovimentacao(),
                m.getQuantidade(),
                m.getObservacao(),
                m.getCriadoEm()
        );
    }
}
