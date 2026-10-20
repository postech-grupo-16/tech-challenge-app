package com.fiap.tech_challenge_backend.estoque.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PecaInsumo")
class PecaInsumoTest {

    private PecaInsumo peca;

    @BeforeEach
    void setUp() {
        peca = PecaInsumo.builder()
                .nome("Filtro de óleo")
                .precoVenda(new BigDecimal("35.00"))
                .precoCompra(new BigDecimal("25.00"))
                .quantidadeEstoque(10)
                .quantidadeMinima(3)
                .build();
    }

    @Nested
    @DisplayName("construção")
    class Construcao {

        @Test
        @DisplayName("deve construir via builder com todos os campos")
        void deveConstruirViaBuilder() {
            assertThat(peca.getNome()).isEqualTo("Filtro de óleo");
            assertThat(peca.getPrecoVenda()).isEqualByComparingTo("35.00");
            assertThat(peca.getPrecoCompra()).isEqualByComparingTo("25.00");
            assertThat(peca.getQuantidadeEstoque()).isEqualTo(10);
            assertThat(peca.getQuantidadeMinima()).isEqualTo(3);
            assertThat(peca.getId()).isNull();
        }

        @Test
        @DisplayName("deve construir via construtor vazio")
        void deveConstruirViaConstrutorVazio() {
            var p = new PecaInsumo();
            assertThat(p).isNotNull();
            assertThat(p.getNome()).isNull();
        }

        @Test
        @DisplayName("deve aceitar campo descricao e quantidadePorUnidade opcionais")
        void deveAceitarCamposOpcionais() {
            var p = PecaInsumo.builder()
                    .nome("Vela")
                    .precoVenda(new BigDecimal("10.00"))
                    .precoCompra(new BigDecimal("6.00"))
                    .quantidadeEstoque(0)
                    .quantidadeMinima(0)
                    .descricao("Vela de ignição NGK")
                    .quantidadePorUnidade("Jogo c/4")
                    .build();

            assertThat(p.getDescricao()).isEqualTo("Vela de ignição NGK");
            assertThat(p.getQuantidadePorUnidade()).isEqualTo("Jogo c/4");
        }
    }

    @Nested
    @DisplayName("entrada")
    class Entrada {

        @Test
        @DisplayName("deve adicionar unidades ao estoque")
        void deveAdicionarUnidades() {
            peca.entrada(5);

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(15);
        }

        @Test
        @DisplayName("deve aceitar entrada de uma unidade")
        void deveAceitarEntradaDeUma() {
            peca.entrada(1);

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(11);
        }

        @Test
        @DisplayName("deve acumular múltiplas entradas")
        void deveAcumularMultiplasEntradas() {
            peca.entrada(5);
            peca.entrada(3);

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(18);
        }
    }

    @Nested
    @DisplayName("saida")
    class Saida {

        @Test
        @DisplayName("deve subtrair unidades do estoque")
        void deveSubtrairUnidades() {
            peca.saida(4);

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(6);
        }

        @Test
        @DisplayName("deve permitir saída que zera o estoque")
        void devePermitirSaidaTotal() {
            peca.saida(10);

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(0);
        }

        @Test
        @DisplayName("deve lançar exceção quando estoque é insuficiente")
        void deveLancarExcecaoQuandoInsuficiente() {
            assertThatThrownBy(() -> peca.saida(11))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Quantidade insuficiente");
        }

        @Test
        @DisplayName("mensagem de erro deve conter o nome da peça")
        void mensagemDeveConterNomeDaPeca() {
            assertThatThrownBy(() -> peca.saida(99))
                    .hasMessageContaining("Filtro de óleo");
        }
    }

    @Nested
    @DisplayName("estoqueAbaixoDoMinimo")
    class EstoqueAbaixoDoMinimo {

        @Test
        @DisplayName("deve retornar true quando estoque < mínimo")
        void deveRetornarTrueQuandoAbaixo() {
            peca.setQuantidadeEstoque(2);

            assertThat(peca.estoqueAbaixoDoMinimo()).isTrue();
        }

        @Test
        @DisplayName("deve retornar false quando estoque == mínimo")
        void deveRetornarFalseQuandoIgual() {
            peca.setQuantidadeEstoque(3);

            assertThat(peca.estoqueAbaixoDoMinimo()).isFalse();
        }

        @Test
        @DisplayName("deve retornar false quando estoque > mínimo")
        void deveRetornarFalseQuandoAcima() {
            assertThat(peca.estoqueAbaixoDoMinimo()).isFalse();
        }

        @Test
        @DisplayName("deve retornar true após saída que leva abaixo do mínimo")
        void deveRetornarTrueAposSaida() {
            peca.saida(8);

            assertThat(peca.estoqueAbaixoDoMinimo()).isTrue();
        }

        @Test
        @DisplayName("deve retornar false após entrada que supera o mínimo")
        void deveRetornarFalseAposEntrada() {
            peca.setQuantidadeEstoque(1);
            peca.entrada(5);

            assertThat(peca.estoqueAbaixoDoMinimo()).isFalse();
        }
    }
}
