package com.fiap.tech_challenge_backend.atendimento.application.ports.out;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;

import java.util.List;
import java.util.UUID;

public interface OsHistoricoStatusRepositoryPort {

    OsHistoricoStatus salvar(OsHistoricoStatus historicoStatus);

    List<OsHistoricoStatus> buscarPorOrdensServicoOrdenado(List<Long> ordemIds);
}

