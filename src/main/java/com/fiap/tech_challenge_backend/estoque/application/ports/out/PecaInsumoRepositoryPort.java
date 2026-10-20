package com.fiap.tech_challenge_backend.estoque.application.ports.out;

import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PecaInsumoRepositoryPort {

    Optional<PecaInsumo> buscarPorId(UUID id);

    PecaInsumo salvar(PecaInsumo peca);

    List<PecaInsumo> buscarTodos();

    List<PecaInsumo> buscarPorTipo(TipoPecaInsumo tipo);

    List<PecaInsumo> buscarAbaixoDoMinimo();
}
