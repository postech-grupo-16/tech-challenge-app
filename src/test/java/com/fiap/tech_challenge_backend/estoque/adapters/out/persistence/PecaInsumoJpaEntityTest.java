package com.fiap.tech_challenge_backend.estoque.adapters.out.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PecaInsumoJpaEntity.validarPrecos")
class PecaInsumoJpaEntityTest {

    private PecaInsumoJpaEntity peca;

    @BeforeEach
    void setUp() {
        peca = PecaInsumoJpaEntity.builder()
                .nome("Filtro de óleo")
                .precoVenda(new BigDecimal("35.00"))
                .precoCompra(new BigDecimal("25.00"))
                .quantidadeEstoque(10)
                .quantidadeMinima(3)
                .build();
    }

    @Test
    @DisplayName("não deve lançar exceção quando precoCompra < precoVenda")
    void naoDeveLancarQuandoCompraMenorQueVenda() {
        assertThatCode(() -> peca.validarPrecos()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("não deve lançar exceção quando precoCompra == precoVenda")
    void naoDeveLancarQuandoCompraIgualVenda() {
        peca.setPrecoCompra(new BigDecimal("35.00"));

        assertThatCode(() -> peca.validarPrecos()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("deve lançar IllegalArgumentException quando precoCompra > precoVenda")
    void deveLancarQuandoCompraMaiorQueVenda() {
        peca.setPrecoCompra(new BigDecimal("40.00"));

        assertThatThrownBy(() -> peca.validarPrecos())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("preço de compra não pode ser maior");
    }

    @Test
    @DisplayName("não deve lançar exceção quando precoCompra é nulo")
    void naoDeveLancarQuandoCompraNull() {
        peca.setPrecoCompra(null);

        assertThatCode(() -> peca.validarPrecos()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("não deve lançar exceção quando precoVenda é nulo")
    void naoDeveLancarQuandoVendaNull() {
        peca.setPrecoVenda(null);

        assertThatCode(() -> peca.validarPrecos()).doesNotThrowAnyException();
    }
}
