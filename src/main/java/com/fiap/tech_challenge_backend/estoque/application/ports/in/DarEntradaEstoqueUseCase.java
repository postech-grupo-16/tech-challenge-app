package com.fiap.tech_challenge_backend.estoque.application.ports.in;

import com.fiap.tech_challenge_backend.estoque.application.dto.EntradaEstoqueRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoResponseDTO;

public interface DarEntradaEstoqueUseCase {

    PecaInsumoResponseDTO darEntrada(EntradaEstoqueRequestDTO request);
}
