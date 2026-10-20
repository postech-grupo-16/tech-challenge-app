package com.fiap.tech_challenge_backend.atendimento.application.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.AprovarRejeitarOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrcamentoResponseDTO;

public interface ResponderOrcamentoUseCase {

    OrcamentoResponseDTO responder(Long ordemServicoId, Long orcamentoId, AprovarRejeitarOrcamentoRequestDTO request);
}
