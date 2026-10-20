package com.fiap.tech_challenge_backend.estoque.application.dto;

import com.fiap.tech_challenge_backend.shared.application.dto.RelatorioResponseDTO;

import java.util.List;

/**
 * Fábricas de {@link RelatorioResponseDTO} com mensagens específicas de estoque.
 * Contexto Delimitado: estoque
 */
public final class EstoqueRelatorioResponseFactory {

    private EstoqueRelatorioResponseFactory() {
    }

    public static <T> RelatorioResponseDTO<T> vazio(String statusBuscado) {
        return new RelatorioResponseDTO<>(
                "Nenhuma peça ou insumo encontrada com o status: " + statusBuscado,
                0,
                List.of()
        );
    }

    public static <T> RelatorioResponseDTO<T> sucesso(List<T> dados) {
        return new RelatorioResponseDTO<>(
                "Estoque carregado com sucesso",
                dados.size(),
                dados
        );
    }

    public static <T> RelatorioResponseDTO<T> entradaSucesso(List<T> dados, Integer quantidade) {
        return new RelatorioResponseDTO<>(
                "Entrada de estoque registrada com sucesso | Quantidade adicionada: " + quantidade,
                dados.size(),
                dados
        );
    }

    public static <T> RelatorioResponseDTO<T> saidaSucesso(List<T> dados, Integer quantidade) {
        return new RelatorioResponseDTO<>(
                "Saída de estoque registrada com sucesso | Quantidade removida: " + quantidade,
                dados.size(),
                dados
        );
    }

    public static <T> RelatorioResponseDTO<T> deleteItemSucesso() {
        return new RelatorioResponseDTO<>(
                "Item de estoque removido com sucesso. Todos os registros de utilização nas ordens de serviço foram preservados no histórico.",
                0,
                List.of()
        );
    }
}
