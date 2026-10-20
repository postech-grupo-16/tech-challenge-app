package com.fiap.tech_challenge_backend.estoque.application.ports.out;

import com.fiap.tech_challenge_backend.estoque.domain.entities.MovimentacaoEstoque;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;

import java.util.List;

public interface MovimentacaoRepositoryPort {

    MovimentacaoEstoque salvar(MovimentacaoEstoque movimentacao);

    List<MovimentacaoEstoque> buscarPorPecaInsumoOrdenadoPorDataDesc(PecaInsumo pecaInsumo);
}
