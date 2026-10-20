package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("OrdemServicoResponseDTO")
class OrdemServicoResponseDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        UUID mecanicoId = UUID.randomUUID();
        ClienteInfoDTO cliente = new ClienteInfoDTO(UUID.randomUUID(), "João", "11999999999", "joao@email.com");
        VeiculoInfoDTO veiculo = new VeiculoInfoDTO("ABC1234", "Ford Ka", "Branco");
        LocalDateTime agora = LocalDateTime.now();

        OrdemServicoResponseDTO dto = new OrdemServicoResponseDTO(
                1L,
                cliente,
                veiculo,
                mecanicoId,
                "Carlos Mecânico",
                StatusOrdemServico.EM_EXECUCAO,
                BigDecimal.valueOf(1000.00),
                "Carro não inicia",
                "Verificar bateria",
                agora,
                agora.plusHours(1),
                agora.plusHours(5),
                true,
                List.of()
        );

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.cliente()).isEqualTo(cliente);
        assertThat(dto.veiculo()).isEqualTo(veiculo);
        assertThat(dto.mecanicoId()).isEqualTo(mecanicoId);
        assertThat(dto.status()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
        assertThat(dto.valorTotal()).isEqualByComparingTo(BigDecimal.valueOf(1000.00));
        assertThat(dto.urgente()).isTrue();
        assertThat(dto.orcamentos()).isEmpty();
    }

    @Test
    @DisplayName("Deve criar instância com campos opcionais null")
    void testCriarComCamposOpcionaisNull() {
        ClienteInfoDTO cliente = new ClienteInfoDTO(UUID.randomUUID(), "Maria", null, null);
        VeiculoInfoDTO veiculo = new VeiculoInfoDTO("XYZ9876", "Chevrolet", "Preto");

        OrdemServicoResponseDTO dto = new OrdemServicoResponseDTO(
                1L,
                cliente,
                veiculo,
                null,
                null,
                StatusOrdemServico.RECEBIDA,
                BigDecimal.ZERO,
                "Queixa",
                null,
                null,
                null,
                null,
                false,
                List.of()
        );

        assertThat(dto).isNotNull();
        assertThat(dto.mecanicoId()).isNull();
        assertThat(dto.mecanicoNome()).isNull();
        assertThat(dto.observacoes()).isNull();
        assertThat(dto.urgente()).isFalse();
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID mecanicoId = UUID.randomUUID();
        ClienteInfoDTO cliente = new ClienteInfoDTO(clienteId, "Cliente A", "11988888888", "a@email.com");
        VeiculoInfoDTO veiculo = new VeiculoInfoDTO("ABC1234", "Ford", "Branco");

        OrdemServicoResponseDTO dto1 = new OrdemServicoResponseDTO(
                1L, cliente, veiculo, mecanicoId, "Mário", StatusOrdemServico.FINALIZADA,
                BigDecimal.valueOf(500.00), "Queixa", "Obs", null, null, null, true, List.of()
        );

        OrdemServicoResponseDTO dto2 = new OrdemServicoResponseDTO(
                1L, cliente, veiculo, mecanicoId, "Mário", StatusOrdemServico.FINALIZADA,
                BigDecimal.valueOf(500.00), "Queixa", "Obs", null, null, null, true, List.of()
        );

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com ids diferentes")
    void testDesigualdadeComIdDiferente() {
        ClienteInfoDTO cliente = new ClienteInfoDTO(UUID.randomUUID(), "Cliente", null, null);
        VeiculoInfoDTO veiculo = new VeiculoInfoDTO("ABC1234", "Ford", "Branco");

        OrdemServicoResponseDTO dto1 = new OrdemServicoResponseDTO(
                1L, cliente, veiculo, null, null, StatusOrdemServico.RECEBIDA,
                BigDecimal.ZERO, "Queixa", null, null, null, null, false, List.of()
        );

        OrdemServicoResponseDTO dto2 = new OrdemServicoResponseDTO(
                2L, cliente, veiculo, null, null, StatusOrdemServico.RECEBIDA,
                BigDecimal.ZERO, "Queixa", null, null, null, null, false, List.of()
        );

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        ClienteInfoDTO cliente = new ClienteInfoDTO(UUID.randomUUID(), "João Silva", null, null);
        VeiculoInfoDTO veiculo = new VeiculoInfoDTO("ABC1234", "Ford", "Branco");

        OrdemServicoResponseDTO dto = new OrdemServicoResponseDTO(
                1L, cliente, veiculo, null, null, StatusOrdemServico.EM_EXECUCAO,
                BigDecimal.ZERO, "Queixa", null, null, null, null, true, List.of()
        );

        String toString = dto.toString();

        assertThat(toString).contains("OrdemServicoResponseDTO");
        assertThat(toString).contains("EM_EXECUCAO");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID mecanicoId = UUID.randomUUID();
        ClienteInfoDTO cliente = new ClienteInfoDTO(clienteId, "Cliente", "11988888888", "a@email.com");
        VeiculoInfoDTO veiculo = new VeiculoInfoDTO("ABC1234", "Ford", "Branco");

        OrdemServicoResponseDTO dto1 = new OrdemServicoResponseDTO(
                1L, cliente, veiculo, mecanicoId, "Mário", StatusOrdemServico.FINALIZADA,
                BigDecimal.valueOf(500.00), "Queixa", "Obs", null, null, null, true, List.of()
        );

        OrdemServicoResponseDTO dto2 = new OrdemServicoResponseDTO(
                1L, cliente, veiculo, mecanicoId, "Mário", StatusOrdemServico.FINALIZADA,
                BigDecimal.valueOf(500.00), "Queixa", "Obs", null, null, null, true, List.of()
        );

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
