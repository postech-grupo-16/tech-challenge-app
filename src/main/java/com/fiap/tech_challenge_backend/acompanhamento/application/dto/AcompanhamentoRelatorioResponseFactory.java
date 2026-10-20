package com.fiap.tech_challenge_backend.acompanhamento.application.dto;

import com.fiap.tech_challenge_backend.shared.application.dto.RelatorioResponseDTO;

import java.util.List;

/**
 * Fábricas de {@link RelatorioResponseDTO} com mensagens específicas de acompanhamento.
 * Contexto Delimitado: acompanhamento
 */
public final class AcompanhamentoRelatorioResponseFactory {

    private AcompanhamentoRelatorioResponseFactory() {
    }

    public static <T> RelatorioResponseDTO<T> sucesso(List<T> dados) {
        return new RelatorioResponseDTO<>(
                "Ordens de serviço do cliente carregadas com sucesso",
                dados.size(),
                dados
        );
    }

    public static <T> RelatorioResponseDTO<T> vazio() {
        return new RelatorioResponseDTO<>(
                "Cliente ainda não possui nenhuma ordem de serviço registrada.",
                0,
                List.of()
        );
    }
}
