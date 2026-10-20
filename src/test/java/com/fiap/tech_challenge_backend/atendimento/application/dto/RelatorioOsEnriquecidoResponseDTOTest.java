package com.fiap.tech_challenge_backend.atendimento.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("RelatorioOsEnriquecidoResponseDTO")
class RelatorioOsEnriquecidoResponseDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        Map<String, String> mapa = new HashMap<>();
        mapa.put("RECEBIDA", "2h");
        mapa.put("EM_DIAGNOSTICO", "4h");

        ClienteInfoDTO cliente = new ClienteInfoDTO(UUID.randomUUID(), "João", "11999999999", "joao@email.com");
        VeiculoInfoDTO veiculo = new VeiculoInfoDTO("ABC1234", "Ford Ka", "Branco");
        UUID mecanicoId = UUID.randomUUID();
        MecanicoInfoDTO mecanico = new MecanicoInfoDTO(mecanicoId, "Carlos", "carlos@email.com", "11987654321");

        RelatorioOsEnriquecidoResponseDTO dto = new RelatorioOsEnriquecidoResponseDTO(
                1L,
                "João Silva",
                "FINALIZADA",
                true,
                "10h 30m",
                mapa,
                BigDecimal.valueOf(1500.00),
                cliente,
                veiculo,
                mecanico,
                List.of()
        );

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.clienteNome()).isEqualTo("João Silva");
        assertThat(dto.statusAtual()).isEqualTo("FINALIZADA");
        assertThat(dto.urgente()).isTrue();
        assertThat(dto.tempoTotalAtendimento()).isEqualTo("10h 30m");
        assertThat(dto.valorTotal()).isEqualByComparingTo(BigDecimal.valueOf(1500.00));
        assertThat(dto.cliente()).isEqualTo(cliente);
        assertThat(dto.veiculo()).isEqualTo(veiculo);
        assertThat(dto.mecanico()).isEqualTo(mecanico);
        assertThat(dto.orcamentos()).isEmpty();
    }

    @Test
    @DisplayName("Deve criar instância com expandidos null")
    void testCriarComExpandidosNull() {
        Map<String, String> mapa = new HashMap<>();
        mapa.put("STATUS", "1h");

        RelatorioOsEnriquecidoResponseDTO dto = new RelatorioOsEnriquecidoResponseDTO(
                1L,
                "Cliente A",
                "ENTREGUE",
                false,
                "5h",
                mapa,
                BigDecimal.valueOf(500.00),
                null,
                null,
                null,
                null
        );

        assertThat(dto).isNotNull();
        assertThat(dto.cliente()).isNull();
        assertThat(dto.veiculo()).isNull();
        assertThat(dto.mecanico()).isNull();
        assertThat(dto.orcamentos()).isNull();
    }

    @Test
    @DisplayName("Deve testar método from")
    void testFrom() {
        Map<String, String> mapa = new HashMap<>();
        mapa.put("STATUS", "1h");

        RelatorioOrdemServicoResponseDTO base = new RelatorioOrdemServicoResponseDTO(
                1L,
                "João",
                "FINALIZADA",
                true,
                "10h",
                mapa
        );

        RelatorioOsEnriquecidoResponseDTO dto = RelatorioOsEnriquecidoResponseDTO.from(
                base,
                BigDecimal.valueOf(1000.00)
        );

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.clienteNome()).isEqualTo("João");
        assertThat(dto.statusAtual()).isEqualTo("FINALIZADA");
        assertThat(dto.valorTotal()).isEqualByComparingTo(BigDecimal.valueOf(1000.00));
        assertThat(dto.cliente()).isNull();
        assertThat(dto.veiculo()).isNull();
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        Map<String, String> mapa1 = new HashMap<>();
        mapa1.put("STATUS", "1h");

        Map<String, String> mapa2 = new HashMap<>();
        mapa2.put("STATUS", "1h");

        RelatorioOsEnriquecidoResponseDTO dto1 = new RelatorioOsEnriquecidoResponseDTO(
                1L, "Cliente A", "FINALIZADA", true, "10h", mapa1, BigDecimal.valueOf(500.00),
                null, null, null, null
        );

        RelatorioOsEnriquecidoResponseDTO dto2 = new RelatorioOsEnriquecidoResponseDTO(
                1L, "Cliente A", "FINALIZADA", true, "10h", mapa2, BigDecimal.valueOf(500.00),
                null, null, null, null
        );

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com ids diferentes")
    void testDesigualdadeComIdDiferente() {
        Map<String, String> mapa = new HashMap<>();

        RelatorioOsEnriquecidoResponseDTO dto1 = new RelatorioOsEnriquecidoResponseDTO(
                1L, "Cliente A", "FINALIZADA", true, "10h", mapa, BigDecimal.valueOf(500.00),
                null, null, null, null
        );

        RelatorioOsEnriquecidoResponseDTO dto2 = new RelatorioOsEnriquecidoResponseDTO(
                2L, "Cliente A", "FINALIZADA", true, "10h", mapa, BigDecimal.valueOf(500.00),
                null, null, null, null
        );

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        Map<String, String> mapa = new HashMap<>();

        RelatorioOsEnriquecidoResponseDTO dto = new RelatorioOsEnriquecidoResponseDTO(
                1L, "Pedro Silva", "FINALIZADA", true, "8h 30m", mapa, BigDecimal.valueOf(1000.00),
                null, null, null, null
        );

        String toString = dto.toString();

        assertThat(toString).contains("RelatorioOsEnriquecidoResponseDTO");
        assertThat(toString).contains("Pedro Silva");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        Map<String, String> mapa1 = new HashMap<>();
        mapa1.put("STATUS", "1h");

        Map<String, String> mapa2 = new HashMap<>();
        mapa2.put("STATUS", "1h");

        RelatorioOsEnriquecidoResponseDTO dto1 = new RelatorioOsEnriquecidoResponseDTO(
                1L, "Cliente", "FINALIZADA", true, "10h", mapa1, BigDecimal.valueOf(500.00),
                null, null, null, null
        );

        RelatorioOsEnriquecidoResponseDTO dto2 = new RelatorioOsEnriquecidoResponseDTO(
                1L, "Cliente", "FINALIZADA", true, "10h", mapa2, BigDecimal.valueOf(500.00),
                null, null, null, null
        );

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
