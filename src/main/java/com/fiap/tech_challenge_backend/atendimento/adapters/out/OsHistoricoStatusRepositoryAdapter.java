package com.fiap.tech_challenge_backend.atendimento.adapters.out;

import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoJpaEntity;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoRepository;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OsHistoricoStatusJpaEntity;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OsHistoricoStatusMapper;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OsHistoricoStatusRepository;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class OsHistoricoStatusRepositoryAdapter implements OsHistoricoStatusRepositoryPort {

    private final OsHistoricoStatusRepository repository;
    private final OrdemServicoRepository ordemServicoRepository;

    public OsHistoricoStatusRepositoryAdapter(OsHistoricoStatusRepository repository,
                                               OrdemServicoRepository ordemServicoRepository) {
        this.repository = repository;
        this.ordemServicoRepository = ordemServicoRepository;
    }

    @Override
    public OsHistoricoStatus salvar(OsHistoricoStatus historicoStatus) {
        OsHistoricoStatusJpaEntity entity = OsHistoricoStatusMapper.toEntity(historicoStatus);
        OrdemServicoJpaEntity ordemServicoRef =
                ordemServicoRepository.getReferenceById(historicoStatus.getOrdemServico().getId());
        entity.setOrdemServico(ordemServicoRef);
        OsHistoricoStatusJpaEntity saved = repository.save(entity);
        return OsHistoricoStatusMapper.toDomain(saved, historicoStatus.getOrdemServico());
    }

    @Override
    public List<OsHistoricoStatus> buscarPorOrdensServicoOrdenado(List<Long> ordemIds) {
        if (ordemIds == null || ordemIds.isEmpty()) {
            return List.of();
        }
        return repository.findByOrdemServicoIdsOrderedWithUsuario(ordemIds).stream()
                .map(entity -> {
                    OrdemServico osRef = OrdemServico.builder()
                            .id(entity.getOrdemServico().getId())
                            .build();
                    return OsHistoricoStatusMapper.toDomain(entity, osRef);
                })
                .toList();
    }
}
