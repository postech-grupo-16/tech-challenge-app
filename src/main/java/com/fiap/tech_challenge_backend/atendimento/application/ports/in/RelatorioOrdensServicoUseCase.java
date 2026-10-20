package com.fiap.tech_challenge_backend.atendimento.application.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.RelatorioOsEnriquecidoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;

import java.util.List;


public interface RelatorioOrdensServicoUseCase {

    List<RelatorioOsEnriquecidoResponseDTO> listarRelatorio(String[] expands);

    List<RelatorioOsEnriquecidoResponseDTO> listarRelatorioPorStatus(StatusOrdemServico status, String[] expands);
}

