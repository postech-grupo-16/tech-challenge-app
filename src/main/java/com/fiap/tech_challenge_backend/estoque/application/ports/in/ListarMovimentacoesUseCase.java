package com.fiap.tech_challenge_backend.estoque.application.ports.in;

import com.fiap.tech_challenge_backend.estoque.application.dto.MovimentacaoResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ListarMovimentacoesUseCase {

    List<MovimentacaoResponseDTO> listarMovimentacoes(UUID pecaInsumoId);
}
