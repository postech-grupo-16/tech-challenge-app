package com.fiap.tech_challenge_backend.estoque.application.ports.in;

import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoResponseDTO;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;

import java.util.List;
import java.util.UUID;

public interface BuscarPecaInsumoUseCase {

    PecaInsumoResponseDTO buscarPorId(UUID id);

    List<PecaInsumoResponseDTO> listarTodos(TipoPecaInsumo tipo);

    List<PecaInsumoResponseDTO> listarAbaixoDoMinimo();
}
