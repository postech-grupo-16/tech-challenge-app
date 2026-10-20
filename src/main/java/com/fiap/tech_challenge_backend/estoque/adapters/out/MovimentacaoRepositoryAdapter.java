package com.fiap.tech_challenge_backend.estoque.adapters.out;

import com.fiap.tech_challenge_backend.estoque.application.ports.out.MovimentacaoRepositoryPort;
import com.fiap.tech_challenge_backend.estoque.domain.entities.MovimentacaoEstoque;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.adapters.out.persistence.MovimentacaoEstoqueMapper;
import com.fiap.tech_challenge_backend.estoque.adapters.out.persistence.MovimentacaoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovimentacaoRepositoryAdapter implements MovimentacaoRepositoryPort {

    private final MovimentacaoRepository repository;

    public MovimentacaoRepositoryAdapter(MovimentacaoRepository repository) {
        this.repository = repository;
    }

    @Override
    public MovimentacaoEstoque salvar(MovimentacaoEstoque movimentacao) {
        var salvo = repository.save(MovimentacaoEstoqueMapper.toEntity(movimentacao));
        return MovimentacaoEstoqueMapper.toDomain(salvo);
    }

    @Override
    public List<MovimentacaoEstoque> buscarPorPecaInsumoOrdenadoPorDataDesc(PecaInsumo pecaInsumo) {
        return repository.findByPecaInsumoIdOrderByCriadoEmDesc(pecaInsumo.getId()).stream()
                .map(MovimentacaoEstoqueMapper::toDomain)
                .toList();
    }
}
