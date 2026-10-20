package com.fiap.tech_challenge_backend.estoque.domain.entities;

import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MovimentacaoEstoque - Domínio")
class MovimentacaoEstoqueTest {

    private PecaInsumo pecaValida() {
        return PecaInsumo.builder()
                .nome("Filtro de Óleo")
                .precoVenda(new BigDecimal("42.00"))
                .precoCompra(new BigDecimal("24.80"))
                .quantidadeEstoque(35)
                .quantidadeMinima(10)
                .build();
    }

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("deve construir movimentação de ENTRADA com todos os campos")
        void deveConstruirEntrada() {
            var peca = pecaValida();

            var mov = MovimentacaoEstoque.builder()
                    .pecaInsumo(peca)
                    .tipoMovimentacao(TipoMovimentacao.ENTRADA)
                    .quantidade(10)
                    .observacao("Compra NF-001")
                    .build();

            assertThat(mov.getPecaInsumo()).isEqualTo(peca);
            assertThat(mov.getTipoMovimentacao()).isEqualTo(TipoMovimentacao.ENTRADA);
            assertThat(mov.getQuantidade()).isEqualTo(10);
            assertThat(mov.getObservacao()).isEqualTo("Compra NF-001");
        }

        @Test
        @DisplayName("deve construir movimentação de VENDA sem observação")
        void deveConstruirVendaSemObservacao() {
            var mov = MovimentacaoEstoque.builder()
                    .pecaInsumo(pecaValida())
                    .tipoMovimentacao(TipoMovimentacao.VENDA)
                    .quantidade(3)
                    .build();

            assertThat(mov.getTipoMovimentacao()).isEqualTo(TipoMovimentacao.VENDA);
            assertThat(mov.getObservacao()).isNull();
        }

        @Test
        @DisplayName("deve construir movimentação de RESERVA")
        void deveConstruirReserva() {
            var mov = MovimentacaoEstoque.builder()
                    .pecaInsumo(pecaValida())
                    .tipoMovimentacao(TipoMovimentacao.RESERVA)
                    .quantidade(5)
                    .observacao("Reserva OS-010")
                    .build();

            assertThat(mov.getTipoMovimentacao()).isEqualTo(TipoMovimentacao.RESERVA);
            assertThat(mov.getQuantidade()).isEqualTo(5);
        }

        @Test
        @DisplayName("deve construir movimentação de AJUSTE")
        void deveConstruirAjuste() {
            var mov = MovimentacaoEstoque.builder()
                    .pecaInsumo(pecaValida())
                    .tipoMovimentacao(TipoMovimentacao.AJUSTE)
                    .quantidade(2)
                    .observacao("Ajuste inventário")
                    .build();

            assertThat(mov.getTipoMovimentacao()).isEqualTo(TipoMovimentacao.AJUSTE);
        }

        @Test
        @DisplayName("criadoEm deve ser nulo antes do @PrePersist")
        void criadoEmNuloAntesDePeristir() {
            var mov = MovimentacaoEstoque.builder()
                    .pecaInsumo(pecaValida())
                    .tipoMovimentacao(TipoMovimentacao.ENTRADA)
                    .quantidade(1)
                    .build();

            assertThat(mov.getCriadoEm()).isNull();
        }
    }


    @Nested
    @DisplayName("TipoMovimentacao")
    class TipoMovimentacaoTests {

        @Test
        @DisplayName("deve conter todos os tipos esperados")
        void deveTerTodosOsTipos() {
            var tipos = TipoMovimentacao.values();

            assertThat(tipos).containsExactlyInAnyOrder(
                    TipoMovimentacao.ENTRADA,
                    TipoMovimentacao.SAIDA,
                    TipoMovimentacao.VENDA,
                    TipoMovimentacao.RESERVA,
                    TipoMovimentacao.AJUSTE
            );
        }

        @Test
        @DisplayName("deve aceitar cada tipo sem erro")
        void deveAceitarCadaTipo() {
            var peca = pecaValida();

            for (var tipo : TipoMovimentacao.values()) {
                var mov = MovimentacaoEstoque.builder()
                        .pecaInsumo(peca)
                        .tipoMovimentacao(tipo)
                        .quantidade(1)
                        .build();

                assertThat(mov.getTipoMovimentacao()).isEqualTo(tipo);
            }
        }
    }
}
