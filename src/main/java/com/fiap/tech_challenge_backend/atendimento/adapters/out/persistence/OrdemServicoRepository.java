package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoRepository extends JpaRepository<OrdemServicoJpaEntity, Long> {

	@Query("""
			SELECT os
			FROM OrdemServico os
			ORDER BY
				os.urgente DESC,
				COALESCE((
					SELECT MIN(
						CASE sc.categoria
							WHEN 'GARANTIA' THEN 1
							WHEN 'DIAGNOSTICO' THEN 2
							WHEN 'CORRETIVA' THEN 3
							WHEN 'PREVENTIVA' THEN 4
							ELSE 5
						END
					)
					FROM OsServico oss
					JOIN oss.orcamento orc
					JOIN oss.servico sc
					WHERE orc.ordemServico = os
				), 5) ASC,
				os.dataCriacao ASC
			""")
	List<OrdemServicoJpaEntity> findAllPrioritized();

	@Query("""
			SELECT os
			FROM OrdemServico os
			WHERE os.status IN (
			    com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico.EM_EXECUCAO,
			    com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico.AGUARDANDO_APROVACAO,
			    com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico.EM_DIAGNOSTICO,
			    com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico.RECEBIDA
			)
			ORDER BY
			    CASE
			        WHEN os.status = com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico.EM_EXECUCAO THEN 1
			        WHEN os.status = com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico.AGUARDANDO_APROVACAO THEN 2
			        WHEN os.status = com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico.EM_DIAGNOSTICO THEN 3
			        WHEN os.status = com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico.RECEBIDA THEN 4
			        ELSE 5
			    END,
			    os.dataCriacao ASC
			""")
	List<OrdemServicoJpaEntity> findAllAtivasPrioritized();

	@Query("""
			SELECT DISTINCT os
			FROM OrdemServico os
			ORDER BY os.urgente DESC, os.dataCriacao ASC
			""")
	List<OrdemServicoJpaEntity> findAllForRelatorio();

	@Query("""
			SELECT os
			FROM OrdemServico os
			WHERE os.status = :status
			ORDER BY os.urgente DESC, os.dataCriacao ASC
			""")
	List<OrdemServicoJpaEntity> findAllByStatusPrioritized(@Param("status") StatusOrdemServico status);

	@Query("""
			SELECT os
			FROM OrdemServico os
			LEFT JOIN FETCH os.orcamentos
			WHERE EXISTS (
				SELECT 1 FROM OsOrcamento orc WHERE orc.ordemServico = os AND orc.id = :orcamentoId
			)
			""")
	Optional<OrdemServicoJpaEntity> findByOrcamentoId(@Param("orcamentoId") Long orcamentoId);

	@EntityGraph(attributePaths = {
			"orcamentos",
			"orcamentos.servicos",
			"orcamentos.servicos.servico",
			"orcamentos.pecas"
	})
	@Override
	Optional<OrdemServicoJpaEntity> findById(Long id);

	@Query("""
			SELECT MAX(os.id)
			FROM OrdemServico os
			""")
	Long findMaxId();

	@Query("""
			SELECT MAX(orc.id)
			FROM OsOrcamento orc
			""")
	Long findMaxOrcamentoId();

	@Query("""
			SELECT os
			FROM OrdemServico os
			WHERE os.clienteId = :clienteId
			ORDER BY os.dataCriacao DESC
			""")
	List<OrdemServicoJpaEntity> findByClienteIdWithDetails(@Param("clienteId") UUID clienteId);
}
