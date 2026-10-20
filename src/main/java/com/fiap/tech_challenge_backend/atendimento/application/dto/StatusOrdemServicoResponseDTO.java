package com.fiap.tech_challenge_backend.atendimento.application.dto;

import java.util.UUID;

public record StatusOrdemServicoResponseDTO(
        UUID ordemServicoId,
        String status,
        String mensagem,
        Boolean urgente
) {
    public static StatusOrdemServicoResponseDTO from(UUID id, String status, Boolean urgente) {
        String mensagem = obterMensagemStatus(status);
        return new StatusOrdemServicoResponseDTO(id, status, mensagem, urgente);
    }

    private static String obterMensagemStatus(String status) {
        return switch (status) {
            case "RECEBIDA" -> "Sua ordem de serviço foi recebida e está na fila";
            case "EM_DIAGNOSTICO" -> "Estamos realizando o diagnóstico do seu veículo";
            case "AGUARDANDO_APROVACAO" -> "Aguardando sua aprovação para prosseguir";
            case "EM_EXECUCAO" -> "Os trabalhos estão em execução";
            case "CONCLUIDA" -> "Sua ordem de serviço foi concluída";
            case "CANCELADA" -> "Sua ordem de serviço foi cancelada";
            default -> "Status desconhecido";
        };
    }
}
