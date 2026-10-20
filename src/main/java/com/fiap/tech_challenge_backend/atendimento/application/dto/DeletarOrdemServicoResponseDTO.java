package com.fiap.tech_challenge_backend.atendimento.application.dto;

import java.time.LocalDateTime;

public record DeletarOrdemServicoResponseDTO(
        Long id,
        String mensagem,
        LocalDateTime dataDelecao,
        String status
) {
    public static DeletarOrdemServicoResponseDTO sucesso(Long id) {
        return new DeletarOrdemServicoResponseDTO(
                id,
                "Ordem de Serviço deletada com sucesso",
                LocalDateTime.now(),
                "DELETADO"
        );
    }
}
