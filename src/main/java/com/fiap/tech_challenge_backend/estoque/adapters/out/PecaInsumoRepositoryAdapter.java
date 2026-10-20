package com.fiap.tech_challenge_backend.estoque.adapters.out;

import com.fiap.tech_challenge_backend.estoque.application.ports.out.PecaInsumoRepositoryPort;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import com.fiap.tech_challenge_backend.estoque.adapters.out.persistence.PecaInsumoMapper;
import com.fiap.tech_challenge_backend.estoque.adapters.out.persistence.PecaInsumoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Nome de bean explícito porque {@code atendimento.adapters.out.PecaInsumoRepositoryAdapter}
 * (adapter de leitura do catálogo de peças, cross-context) tem o mesmo nome de classe simples
 * e colidiria com o nome padrão gerado pelo Spring a partir dele.
 */
@Component("estoquePecaInsumoRepositoryAdapter")
public class PecaInsumoRepositoryAdapter implements PecaInsumoRepositoryPort {

    private final PecaInsumoRepository repository;

    public PecaInsumoRepositoryAdapter(PecaInsumoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<PecaInsumo> buscarPorId(UUID id) {
        return repository.findById(id).map(PecaInsumoMapper::toDomain);
    }

    @Override
    public PecaInsumo salvar(PecaInsumo peca) {
        var salvo = repository.save(PecaInsumoMapper.toEntity(peca));
        return PecaInsumoMapper.toDomain(salvo);
    }

    @Override
    public List<PecaInsumo> buscarTodos() {
        return repository.findAll().stream().map(PecaInsumoMapper::toDomain).toList();
    }

    @Override
    public List<PecaInsumo> buscarPorTipo(TipoPecaInsumo tipo) {
        return repository.findByTipo(tipo).stream().map(PecaInsumoMapper::toDomain).toList();
    }

    @Override
    public List<PecaInsumo> buscarAbaixoDoMinimo() {
        return repository.findAbaixoDoMinimo().stream().map(PecaInsumoMapper::toDomain).toList();
    }
}
