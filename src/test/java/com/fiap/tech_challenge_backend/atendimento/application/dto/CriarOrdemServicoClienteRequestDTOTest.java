package com.fiap.tech_challenge_backend.atendimento.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CriarOrdemServicoClienteRequestDTO")
class CriarOrdemServicoClienteRequestDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        CriarOrdemServicoClienteRequestDTO dto = new CriarOrdemServicoClienteRequestDTO(
                "12345678901",
                "ABC1234",
                "Carro não está iniciando",
                "Verificar bateria primeiro",
                true
        );

        assertThat(dto).isNotNull();
        assertThat(dto.cpfCnpjCliente()).isEqualTo("12345678901");
        assertThat(dto.placaVeiculo()).isEqualTo("ABC1234");
        assertThat(dto.queixaCliente()).isEqualTo("Carro não está iniciando");
        assertThat(dto.observacoes()).isEqualTo("Verificar bateria primeiro");
        assertThat(dto.urgente()).isTrue();
    }

    @Test
    @DisplayName("Deve criar instância com observações e urgente null")
    void testCriarComOpcionaisNull() {
        CriarOrdemServicoClienteRequestDTO dto = new CriarOrdemServicoClienteRequestDTO(
                "98765432100",
                "XYZ9876",
                "Barulho no motor",
                null,
                null
        );

        assertThat(dto).isNotNull();
        assertThat(dto.cpfCnpjCliente()).isEqualTo("98765432100");
        assertThat(dto.placaVeiculo()).isEqualTo("XYZ9876");
        assertThat(dto.queixaCliente()).isEqualTo("Barulho no motor");
        assertThat(dto.observacoes()).isNull();
        assertThat(dto.urgente()).isNull();
    }

    @Test
    @DisplayName("Deve criar instância com urgente false")
    void testCriarComUrgenteFalse() {
        CriarOrdemServicoClienteRequestDTO dto = new CriarOrdemServicoClienteRequestDTO(
                "11122233344",
                "DEF5678",
                "Revisão de rotina",
                "Sem problemas aparentes",
                false
        );

        assertThat(dto).isNotNull();
        assertThat(dto.urgente()).isFalse();
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        CriarOrdemServicoClienteRequestDTO dto1 = new CriarOrdemServicoClienteRequestDTO(
                "12345678901",
                "ABC1234",
                "Queixa A",
                "Obs A",
                true
        );

        CriarOrdemServicoClienteRequestDTO dto2 = new CriarOrdemServicoClienteRequestDTO(
                "12345678901",
                "ABC1234",
                "Queixa A",
                "Obs A",
                true
        );

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com cpf diferentes")
    void testDesigualdadeComCpfDiferente() {
        CriarOrdemServicoClienteRequestDTO dto1 = new CriarOrdemServicoClienteRequestDTO(
                "12345678901",
                "ABC1234",
                "Queixa A",
                null,
                true
        );

        CriarOrdemServicoClienteRequestDTO dto2 = new CriarOrdemServicoClienteRequestDTO(
                "98765432100",
                "ABC1234",
                "Queixa A",
                null,
                true
        );

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        CriarOrdemServicoClienteRequestDTO dto = new CriarOrdemServicoClienteRequestDTO(
                "12345678901",
                "ABC1234",
                "Problema no ar condicionado",
                "Falha intermitente",
                false
        );
        String toString = dto.toString();

        assertThat(toString).contains("CriarOrdemServicoClienteRequestDTO");
        assertThat(toString).contains("ABC1234");
        assertThat(toString).contains("12345678901");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        CriarOrdemServicoClienteRequestDTO dto1 = new CriarOrdemServicoClienteRequestDTO(
                "12345678901",
                "ABC1234",
                "Queixa",
                "Obs",
                true
        );

        CriarOrdemServicoClienteRequestDTO dto2 = new CriarOrdemServicoClienteRequestDTO(
                "12345678901",
                "ABC1234",
                "Queixa",
                "Obs",
                true
        );

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
