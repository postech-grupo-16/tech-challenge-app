package com.fiap.tech_challenge_backend.atendimento.application.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.DeletarOrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoAtualizarRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;

/**
 * Porta de entrada (Use Case) para atualização e remoção de Ordem de Serviço.
 * Camada: Ports/In (Hexagonal Architecture)
 */
public interface AtualizarOrdemServicoUseCase {

    OrdemServicoResponseDTO atualizar(Long id, OrdemServicoAtualizarRequestDTO request);

    DeletarOrdemServicoResponseDTO remover(Long id);
}


