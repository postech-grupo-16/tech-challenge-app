package com.fiap.tech_challenge_backend.atendimento.application.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.CriarOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrcamentoResponseDTO;

public interface CriarOrcamentoUseCase {

    OrcamentoResponseDTO criar(Long ordemServicoId, CriarOrcamentoRequestDTO request);
}
