package com.fiap.tech_challenge_backend.atendimento.adapters.out;

import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.ServicoCatalogoMapper;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.ServicoCatalogoRepository;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.ServicoCatalogoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.ServicoCatalogo;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ServicoCatalogoRepositoryAdapter implements ServicoCatalogoRepositoryPort {

    private final ServicoCatalogoRepository repository;

    public ServicoCatalogoRepositoryAdapter(ServicoCatalogoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ServicoCatalogo> buscarPorId(UUID id) {
        return repository.findById(id).map(ServicoCatalogoMapper::toDomain);
    }
}
