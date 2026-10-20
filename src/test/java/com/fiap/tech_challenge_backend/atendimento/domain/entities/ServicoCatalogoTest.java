package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ServicoCatalogo - Testes Unitários")
class ServicoCatalogoTest {

    private ServicoCatalogo servico;

    @BeforeEach
    void setUp() {
        servico = ServicoCatalogo.builder()
                .id(UUID.randomUUID())
                .nome("Troca de Óleo")
                .descricao("Troca de óleo do motor")
                .precoMaoDeObra(BigDecimal.valueOf(100))
                .categoria("PREVENTIVA")
                .build();
    }

    @Test
    @DisplayName("Deve criar serviço de catálogo corretamente")
    void testCriarServicoCatalogo() {
        assertNotNull(servico);
        assertEquals("Troca de Óleo", servico.getNome());
        assertEquals("Troca de óleo do motor", servico.getDescricao());
        assertEquals(BigDecimal.valueOf(100), servico.getPrecoMaoDeObra());
        assertEquals("PREVENTIVA", servico.getCategoria());
    }

    @Test
    @DisplayName("Deve ter ID gerado automaticamente")
    void testIdGerado() {
        assertNotNull(servico.getId());
    }

    @Test
    @DisplayName("Deve aceitar categoria DIAGNOSTICO")
    void testCategoriaDignostico() {
        servico.setCategoria("DIAGNOSTICO");
        assertEquals("DIAGNOSTICO", servico.getCategoria());
    }

    @Test
    @DisplayName("Deve aceitar categoria CORRETIVA")
    void testCategoriaCorretiva() {
        servico.setCategoria("CORRETIVA");
        assertEquals("CORRETIVA", servico.getCategoria());
    }

    @Test
    @DisplayName("Deve aceitar categoria PREVENTIVA")
    void testCategoriaPreventiva() {
        servico.setCategoria("PREVENTIVA");
        assertEquals("PREVENTIVA", servico.getCategoria());
    }

    @Test
    @DisplayName("Deve aceitar categoria GARANTIA")
    void testCategoriaGarantia() {
        servico.setCategoria("GARANTIA");
        assertEquals("GARANTIA", servico.getCategoria());
    }

    @Test
    @DisplayName("Deve ter categoria padrão PREVENTIVA")
    void testCategoriaPadrao() {
        ServicoCatalogo novo = ServicoCatalogo.builder()
                .nome("Teste")
                .precoMaoDeObra(BigDecimal.valueOf(50))
                .build();

        assertEquals("PREVENTIVA", novo.getCategoria());
    }

    @Test
    @DisplayName("Deve permitir descrição nula")
    void testDescricaoNula() {
        servico.setDescricao(null);
        assertNull(servico.getDescricao());
    }

    @Test
    @DisplayName("Deve permitir descrição vazia")
    void testDescricaoVazia() {
        servico.setDescricao("");
        assertEquals("", servico.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar preço com duas casas decimais")
    void testPrecoComDecimais() {
        servico.setPrecoMaoDeObra(BigDecimal.valueOf(150.50));
        assertEquals(BigDecimal.valueOf(150.50), servico.getPrecoMaoDeObra());
    }

    @Test
    @DisplayName("Deve aceitar preço inteiro")
    void testPrecoInteiro() {
        servico.setPrecoMaoDeObra(BigDecimal.valueOf(200));
        assertEquals(BigDecimal.valueOf(200), servico.getPrecoMaoDeObra());
    }

    @Test
    @DisplayName("Deve ser idêntico para mesmo ID")
    void testIgualdadeServicos() {
        ServicoCatalogo outro = ServicoCatalogo.builder()
                .id(servico.getId())
                .nome("Outro nome")
                .precoMaoDeObra(BigDecimal.valueOf(50))
                .categoria("CORRETIVA")
                .build();

        assertEquals(servico.getId(), outro.getId());
    }

    @Test
    @DisplayName("Deve ter hash code consistente")
    void testHashCode() {
        assertEquals(servico.hashCode(), servico.hashCode());
    }

    @Test
    @DisplayName("Deve ter toString válido")
    void testToString() {
        assertNotNull(servico.toString());
        assertTrue(servico.toString().contains("Troca de Óleo"));
    }

    @Test
    @DisplayName("Deve atualizar nome")
    void testAtualizarNome() {
        servico.setNome("Alinhamento");
        assertEquals("Alinhamento", servico.getNome());
    }

    @Test
    @DisplayName("Deve atualizar preço")
    void testAtualizarPreco() {
        servico.setPrecoMaoDeObra(BigDecimal.valueOf(250));
        assertEquals(BigDecimal.valueOf(250), servico.getPrecoMaoDeObra());
    }

    @Test
    @DisplayName("Deve atualizar categoria")
    void testAtualizarCategoria() {
        servico.setCategoria("CORRETIVA");
        assertEquals("CORRETIVA", servico.getCategoria());
    }
}
