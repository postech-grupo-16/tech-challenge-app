package com.fiap.tech_challenge_backend.atendimento.application.ports.out;

import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;

import java.util.Optional;
import java.util.UUID;

public interface PecaInsumoCatalogoRepositoryPort {

    Optional<PecaInsumo> buscarPorId(UUID id);
}
