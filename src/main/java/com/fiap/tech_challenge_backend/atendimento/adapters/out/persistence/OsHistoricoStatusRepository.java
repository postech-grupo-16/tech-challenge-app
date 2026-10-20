package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OsHistoricoStatusRepository extends JpaRepository<OsHistoricoStatusJpaEntity, UUID> {

    List<OsHistoricoStatusJpaEntity> findByOrdemServicoIdOrderByDataMudancaAsc(Long ordemServicoId);

    @Query("""
            SELECT h
            FROM OsHistoricoStatus h
            WHERE h.ordemServico.id IN :ordemIds
            ORDER BY h.ordemServico.id ASC, h.dataMudanca ASC
            """)
    List<OsHistoricoStatusJpaEntity> findByOrdemServicoIdsOrderedWithUsuario(@Param("ordemIds") Collection<Long> ordemIds);
}


