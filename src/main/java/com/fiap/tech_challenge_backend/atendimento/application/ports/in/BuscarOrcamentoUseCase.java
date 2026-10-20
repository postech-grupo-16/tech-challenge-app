package com.fiap.tech_challenge_backend.atendimento.application.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.OrcamentoResponseDTO;

public interface BuscarOrcamentoUseCase {

    OrcamentoResponseDTO buscarPorId(Long ordemServicoId, Long orcamentoId);
}
