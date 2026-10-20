package com.fiap.tech_challenge_backend.estoque.application.ports.in;

import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoResponseDTO;

public interface CadastrarPecaInsumoUseCase {

    PecaInsumoResponseDTO cadastrar(PecaInsumoRequestDTO request);
}
