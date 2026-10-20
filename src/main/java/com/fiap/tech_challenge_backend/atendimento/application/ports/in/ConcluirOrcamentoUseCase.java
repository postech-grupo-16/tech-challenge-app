package com.fiap.tech_challenge_backend.atendimento.application.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.ConcluirOrcamentoRequestDTO;

public interface ConcluirOrcamentoUseCase {

    void concluirEEnviar(ConcluirOrcamentoRequestDTO request);
}
