package com.fiap.tech_challenge_backend.estoque.adapters.out.persistence;

import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PecaInsumoRepository extends JpaRepository<PecaInsumoJpaEntity, UUID> {

    @Query("SELECT p FROM PecaInsumo p WHERE p.quantidadeEstoque < p.quantidadeMinima")
    List<PecaInsumoJpaEntity> findAbaixoDoMinimo();

    List<PecaInsumoJpaEntity> findByTipo(TipoPecaInsumo tipo);
}
