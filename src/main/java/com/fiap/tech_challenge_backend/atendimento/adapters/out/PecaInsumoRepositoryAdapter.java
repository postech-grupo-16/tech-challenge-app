package com.fiap.tech_challenge_backend.atendimento.adapters.out;

import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PecaInsumoCatalogoRepositoryPort;
import com.fiap.tech_challenge_backend.estoque.application.ports.out.PecaInsumoRepositoryPort;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PecaInsumoRepositoryAdapter implements PecaInsumoCatalogoRepositoryPort {

    private final PecaInsumoRepositoryPort estoqueRepository;

    public PecaInsumoRepositoryAdapter(PecaInsumoRepositoryPort estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    @Override
    public Optional<PecaInsumo> buscarPorId(UUID id) {
        return estoqueRepository.buscarPorId(id);
    }
}
