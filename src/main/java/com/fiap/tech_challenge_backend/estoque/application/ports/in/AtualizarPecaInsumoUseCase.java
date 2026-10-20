package com.fiap.tech_challenge_backend.estoque.application.ports.in;

import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoResponseDTO;

import java.util.UUID;

public interface AtualizarPecaInsumoUseCase {

    PecaInsumoResponseDTO atualizar(UUID id, PecaInsumoRequestDTO request);

    void remover(UUID id);
}
