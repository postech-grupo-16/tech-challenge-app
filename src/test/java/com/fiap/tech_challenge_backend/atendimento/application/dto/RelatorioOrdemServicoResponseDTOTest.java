package com.fiap.tech_challenge_backend.atendimento.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("RelatorioOrdemServicoResponseDTO")
class RelatorioOrdemServicoResponseDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        Map<String, String> tempoPorStatus = new HashMap<>();
        tempoPorStatus.put("RECEBIDA", "2h");
        tempoPorStatus.put("EM_DIAGNOSTICO", "4h");

        RelatorioOrdemServicoResponseDTO dto = new RelatorioOrdemServicoResponseDTO(
                1L,
                "João Silva",
                "FINALIZADA",
                true,
                "10h 30m",
                tempoPorStatus
        );

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.clienteNome()).isEqualTo("João Silva");
        assertThat(dto.statusAtual()).isEqualTo("FINALIZADA");
        assertThat(dto.urgente()).isTrue();
        assertThat(dto.tempoTotalAtendimento()).isEqualTo("10h 30m");
        assertThat(dto.tempoPorStatus()).hasSize(2);
    }

    @Test
    @DisplayName("Deve criar instância com mapa de tempo vazio")
    void testCriarComMapaVazio() {
        RelatorioOrdemServicoResponseDTO dto = new RelatorioOrdemServicoResponseDTO(
                2L,
                "Maria",
                "ENTREGUE",
                false,
                "5h",
                new HashMap<>()
        );

        assertThat(dto).isNotNull();
        assertThat(dto.tempoPorStatus()).isEmpty();
        assertThat(dto.urgente()).isFalse();
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        Map<String, String> mapa1 = new HashMap<>();
        mapa1.put("STATUS1", "1h");

        Map<String, String> mapa2 = new HashMap<>();
        mapa2.put("STATUS1", "1h");

        RelatorioOrdemServicoResponseDTO dto1 = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente A",
                "FINALIZADA",
                true,
                "10h",
                mapa1
        );

        RelatorioOrdemServicoResponseDTO dto2 = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente A",
                "FINALIZADA",
                true,
                "10h",
                mapa2
        );

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com ids diferentes")
    void testDesigualdadeComIdDiferente() {
        Map<String, String> mapa = new HashMap<>();
        mapa.put("STATUS", "1h");

        RelatorioOrdemServicoResponseDTO dto1 = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente A",
                "FINALIZADA",
                true,
                "10h",
                mapa
        );

        RelatorioOrdemServicoResponseDTO dto2 = new RelatorioOrdemServicoResponseDTO(
                2L,
                "Cliente A",
                "FINALIZADA",
                true,
                "10h",
                mapa
        );

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        Map<String, String> mapa = new HashMap<>();
        mapa.put("STATUS", "1h");

        RelatorioOrdemServicoResponseDTO dto = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Pedro Silva",
                "FINALIZADA",
                true,
                "8h 30m",
                mapa
        );

        String toString = dto.toString();

        assertThat(toString).contains("RelatorioOrdemServicoResponseDTO");
        assertThat(toString).contains("Pedro Silva");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        Map<String, String> mapa1 = new HashMap<>();
        mapa1.put("STATUS", "1h");

        Map<String, String> mapa2 = new HashMap<>();
        mapa2.put("STATUS", "1h");

        RelatorioOrdemServicoResponseDTO dto1 = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente",
                "FINALIZADA",
                true,
                "10h",
                mapa1
        );

        RelatorioOrdemServicoResponseDTO dto2 = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente",
                "FINALIZADA",
                true,
                "10h",
                mapa2
        );

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
