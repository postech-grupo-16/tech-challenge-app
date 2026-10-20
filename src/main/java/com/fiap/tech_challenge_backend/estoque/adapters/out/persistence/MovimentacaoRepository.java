package com.fiap.tech_challenge_backend.estoque.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MovimentacaoRepository extends JpaRepository<MovimentacaoEstoqueJpaEntity, UUID> {

    List<MovimentacaoEstoqueJpaEntity> findByPecaInsumoIdOrderByCriadoEmDesc(UUID pecaInsumoId);
}
