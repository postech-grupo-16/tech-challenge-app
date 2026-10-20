package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("OrdemServicoAtualizarRequestDTO")
class OrdemServicoAtualizarRequestDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID mecanicoId = UUID.randomUUID();
        LocalDateTime dataInicio = LocalDateTime.now();
        LocalDateTime dataFim = LocalDateTime.now().plusHours(5);

        OrdemServicoAtualizarRequestDTO dto = new OrdemServicoAtualizarRequestDTO(
                clienteId,
                veiculoId,
                mecanicoId,
                StatusOrdemServico.EM_EXECUCAO,
                dataInicio,
                dataFim,
                true
        );

        assertThat(dto).isNotNull();
        assertThat(dto.clienteId()).isEqualTo(clienteId);
        assertThat(dto.veiculoId()).isEqualTo(veiculoId);
        assertThat(dto.mecanicoId()).isEqualTo(mecanicoId);
        assertThat(dto.status()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
        assertThat(dto.dataInicioExecucao()).isEqualTo(dataInicio);
        assertThat(dto.dataFinalizacao()).isEqualTo(dataFim);
        assertThat(dto.urgente()).isTrue();
    }

    @Test
    @DisplayName("Deve criar instância com datas null")
    void testCriarComDatasNull() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoAtualizarRequestDTO dto = new OrdemServicoAtualizarRequestDTO(
                clienteId,
                veiculoId,
                null,
                StatusOrdemServico.RECEBIDA,
                null,
                null,
                false
        );

        assertThat(dto).isNotNull();
        assertThat(dto.dataInicioExecucao()).isNull();
        assertThat(dto.dataFinalizacao()).isNull();
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        LocalDateTime dataInicio = LocalDateTime.now();
        LocalDateTime dataFim = LocalDateTime.now().plusHours(5);

        OrdemServicoAtualizarRequestDTO dto1 = new OrdemServicoAtualizarRequestDTO(
                clienteId, veiculoId, null, StatusOrdemServico.EM_DIAGNOSTICO, dataInicio, dataFim, true
        );
        OrdemServicoAtualizarRequestDTO dto2 = new OrdemServicoAtualizarRequestDTO(
                clienteId, veiculoId, null, StatusOrdemServico.EM_DIAGNOSTICO, dataInicio, dataFim, true
        );

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com status diferentes")
    void testDesigualdadeComStatusDiferente() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoAtualizarRequestDTO dto1 = new OrdemServicoAtualizarRequestDTO(
                clienteId, veiculoId, null, StatusOrdemServico.RECEBIDA, null, null, true
        );
        OrdemServicoAtualizarRequestDTO dto2 = new OrdemServicoAtualizarRequestDTO(
                clienteId, veiculoId, null, StatusOrdemServico.FINALIZADA, null, null, true
        );

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoAtualizarRequestDTO dto = new OrdemServicoAtualizarRequestDTO(
                clienteId, veiculoId, null, StatusOrdemServico.EM_EXECUCAO, null, null, true
        );
        String toString = dto.toString();

        assertThat(toString).contains("OrdemServicoAtualizarRequestDTO");
        assertThat(toString).contains("EM_EXECUCAO");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoAtualizarRequestDTO dto1 = new OrdemServicoAtualizarRequestDTO(
                clienteId, veiculoId, null, StatusOrdemServico.FINALIZADA, null, null, true
        );
        OrdemServicoAtualizarRequestDTO dto2 = new OrdemServicoAtualizarRequestDTO(
                clienteId, veiculoId, null, StatusOrdemServico.FINALIZADA, null, null, true
        );

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
