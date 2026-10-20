package com.fiap.tech_challenge_backend.atendimento.application.ports.out;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;

public interface PdfGeneratorPort {

    byte[] gerarDocumentoTexto(OsOrcamento orcamento, Veiculo veiculo);
}
