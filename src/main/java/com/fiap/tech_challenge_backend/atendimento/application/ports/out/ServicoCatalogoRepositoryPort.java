package com.fiap.tech_challenge_backend.atendimento.application.ports.out;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.ServicoCatalogo;

import java.util.Optional;
import java.util.UUID;

public interface ServicoCatalogoRepositoryPort {

    Optional<ServicoCatalogo> buscarPorId(UUID id);
}
