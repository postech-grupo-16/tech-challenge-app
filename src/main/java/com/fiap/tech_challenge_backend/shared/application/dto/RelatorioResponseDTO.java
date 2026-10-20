package com.fiap.tech_challenge_backend.shared.application.dto;

import java.util.List;

public record RelatorioResponseDTO<T>(
        String mensagem,
        Integer totalResultados,
        List<T> dados
) {
    public static <T> RelatorioResponseDTO<T> sucesso(List<T> dados) {
        return new RelatorioResponseDTO<>(
                "Relatório carregado com sucesso",
                dados.size(),
                dados
        );
    }

    public static <T> RelatorioResponseDTO<T> vazio(String statusBuscado) {
        return new RelatorioResponseDTO<>(
                "Nenhuma ordem de serviço encontrada com o status: " + statusBuscado,
                0,
                List.of()
        );
    }

    public static <T> RelatorioResponseDTO<T> oSsucesso(List<T> dados) {
        return new RelatorioResponseDTO<>(
                "Ordem de Serviço carregada com sucesso",
                dados.size(),
                dados
        );
    }

    public static <T> RelatorioResponseDTO<T> alterarSucesso(List<T> dados) {
        return new RelatorioResponseDTO<>(
                "Ordem de serviço alterada com sucesso",
                dados.size(),
                dados
        );
    }

    public static <T> RelatorioResponseDTO<T> deleteSucesso(String tipoRecurso) {
        return new RelatorioResponseDTO<>(
                tipoRecurso + " removido com sucesso. O histórico associado foi preservado para consultas futuras.",
                0,
                List.of()
        );
    }

    public static <T> RelatorioResponseDTO<T> deleteVeiculoSucesso() {
        return new RelatorioResponseDTO<>(
                "Veículo removido com sucesso. Todas as ordens de serviço associadas ao veículo foram preservadas no histórico.",
                0,
                List.of()
        );
    }

}
