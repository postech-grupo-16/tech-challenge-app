package com.fiap.tech_challenge_backend.atendimento.application.ports.in;

import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;

import java.util.List;


public interface BuscarOrdemServicoUseCase {

    OrdemServicoResponseDTO buscarPorId(Long id);

    List<OrdemServicoResponseDTO> listarTodos();

    List<OrdemServicoResponseDTO> listarAtivasPriorizadas();
}

