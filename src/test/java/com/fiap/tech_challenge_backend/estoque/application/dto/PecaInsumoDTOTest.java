package com.fiap.tech_challenge_backend.estoque.application.dto;

import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTOs de PecaInsumo")
class PecaInsumoDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // ─────────────────────────────────────────────
    // PecaInsumoRequestDTO
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("PecaInsumoRequestDTO")
    class Request {

        @Test
        @DisplayName("deve construir com todos os campos")
        void deveConstruirComTodosOsCampos() {
            var dto = new PecaInsumoRequestDTO(
                    "Filtro de óleo", "Filtro original",
                    new BigDecimal("35.00"), new BigDecimal("20.00"),
                    "Unidade", 10, 3, TipoPecaInsumo.PECA
            );

            assertThat(dto.nome()).isEqualTo("Filtro de óleo");
            assertThat(dto.descricao()).isEqualTo("Filtro original");
            assertThat(dto.precoVenda()).isEqualByComparingTo("35.00");
            assertThat(dto.precoCompra()).isEqualByComparingTo("20.00");
            assertThat(dto.quantidadePorUnidade()).isEqualTo("Unidade");
            assertThat(dto.quantidadeEstoque()).isEqualTo(10);
            assertThat(dto.quantidadeMinima()).isEqualTo(3);
            assertThat(dto.tipo()).isEqualTo(TipoPecaInsumo.PECA);
        }

        @Test
        @DisplayName("deve aceitar campos opcionais nulos")
        void deveAceitarCamposOpcionaisNulos() {
            var dto = new PecaInsumoRequestDTO(
                    "Vela", null,
                    new BigDecimal("10.00"), new BigDecimal("6.00"),
                    null, 0, 0, TipoPecaInsumo.PECA
            );

            assertThat(dto.descricao()).isNull();
            assertThat(dto.quantidadePorUnidade()).isNull();
            assertThat(validator.validate(dto)).isEmpty();
        }

        @Test
        @DisplayName("deve passar na validação quando todos os campos obrigatórios são válidos")
        void devePassarNaValidacaoComDadosValidos() {
            var dto = new PecaInsumoRequestDTO(
                    "Pastilha de freio", null,
                    new BigDecimal("80.00"), new BigDecimal("50.00"),
                    null, 5, 2, TipoPecaInsumo.PECA
            );

            assertThat(validator.validate(dto)).isEmpty();
        }

        @Test
        @DisplayName("deve aceitar tipo INSUMO")
        void deveAceitarTipoInsumo() {
            var dto = new PecaInsumoRequestDTO(
                    "Óleo 5W30", null,
                    new BigDecimal("50.00"), new BigDecimal("30.00"),
                    "Litro", 20, 5, TipoPecaInsumo.INSUMO
            );

            assertThat(dto.tipo()).isEqualTo(TipoPecaInsumo.INSUMO);
            assertThat(validator.validate(dto)).isEmpty();
        }

        @Test
        @DisplayName("deve falhar quando tipo é nulo")
        void deveFalharQuandoTipoNulo() {
            var dto = new PecaInsumoRequestDTO(
                    "Filtro", null,
                    new BigDecimal("10.00"), new BigDecimal("6.00"),
                    null, 0, 0, null
            );

            var violacoes = validator.validate(dto);
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("tipo"));
        }

        @Test
        @DisplayName("deve falhar quando nome é blank")
        void deveFalharQuandoNomeBlank() {
            var dto = new PecaInsumoRequestDTO(
                    "  ", null,
                    new BigDecimal("10.00"), new BigDecimal("6.00"),
                    null, 0, 0, TipoPecaInsumo.PECA
            );

            var violacoes = validator.validate(dto);
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("nome"));
        }

        @Test
        @DisplayName("deve falhar quando nome é nulo")
        void deveFalharQuandoNomeNulo() {
            var dto = new PecaInsumoRequestDTO(
                    null, null,
                    new BigDecimal("10.00"), new BigDecimal("6.00"),
                    null, 0, 0, TipoPecaInsumo.PECA
            );

            var violacoes = validator.validate(dto);
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("nome"));
        }

        @Test
        @DisplayName("deve falhar quando precoVenda é nulo")
        void deveFalharQuandoPrecoVendaNulo() {
            var dto = new PecaInsumoRequestDTO("Filtro", null, null, new BigDecimal("6.00"), null, 0, 0, TipoPecaInsumo.PECA);

            var violacoes = validator.validate(dto);
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("precoVenda"));
        }

        @Test
        @DisplayName("deve falhar quando precoVenda é zero ou negativo")
        void deveFalharQuandoPrecoVendaNaoPositivo() {
            var dto = new PecaInsumoRequestDTO("Filtro", null, BigDecimal.ZERO, new BigDecimal("6.00"), null, 0, 0, TipoPecaInsumo.PECA);

            var violacoes = validator.validate(dto);
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("precoVenda"));
        }

        @Test
        @DisplayName("deve falhar quando precoCompra é nulo")
        void deveFalharQuandoPrecoCompraNulo() {
            var dto = new PecaInsumoRequestDTO("Filtro", null, new BigDecimal("10.00"), null, null, 0, 0, TipoPecaInsumo.PECA);

            var violacoes = validator.validate(dto);
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("precoCompra"));
        }

        @Test
        @DisplayName("deve falhar quando quantidadeEstoque é nulo")
        void deveFalharQuandoQuantidadeEstoqueNula() {
            var dto = new PecaInsumoRequestDTO("Filtro", null,
                    new BigDecimal("10.00"), new BigDecimal("6.00"), null, null, 0, TipoPecaInsumo.PECA);

            var violacoes = validator.validate(dto);
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("quantidadeEstoque"));
        }

        @Test
        @DisplayName("deve falhar quando quantidadeEstoque é negativo")
        void deveFalharQuandoQuantidadeEstoqueNegativa() {
            var dto = new PecaInsumoRequestDTO("Filtro", null,
                    new BigDecimal("10.00"), new BigDecimal("6.00"), null, -1, 0, TipoPecaInsumo.PECA);

            var violacoes = validator.validate(dto);
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("quantidadeEstoque"));
        }

        @Test
        @DisplayName("deve falhar quando quantidadeMinima é nula")
        void deveFalharQuandoQuantidadeMiNimaNula() {
            var dto = new PecaInsumoRequestDTO("Filtro", null,
                    new BigDecimal("10.00"), new BigDecimal("6.00"), null, 0, null, TipoPecaInsumo.PECA);

            var violacoes = validator.validate(dto);
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("quantidadeMinima"));
        }

        @Test
        @DisplayName("deve falhar quando quantidadeMinima é negativa")
        void deveFalharQuandoQuantidadeMinimaaNegativa() {
            var dto = new PecaInsumoRequestDTO("Filtro", null,
                    new BigDecimal("10.00"), new BigDecimal("6.00"), null, 0, -1, TipoPecaInsumo.PECA);

            var violacoes = validator.validate(dto);
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("quantidadeMinima"));
        }
    }

    // ─────────────────────────────────────────────
    // PecaInsumoResponseDTO
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("PecaInsumoResponseDTO")
    class Response {

        private PecaInsumo pecaCompleta() {
            return PecaInsumo.builder()
                    .id(UUID.fromString("aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa"))
                    .nome("Filtro de óleo")
                    .descricao("Filtro original")
                    .precoVenda(new BigDecimal("35.00"))
                    .precoCompra(new BigDecimal("20.00"))
                    .quantidadePorUnidade("Unidade")
                    .quantidadeEstoque(10)
                    .quantidadeMinima(3)
                    .tipo(TipoPecaInsumo.PECA)
                    .build();
        }

        @Test
        @DisplayName("from() deve mapear todos os campos da entidade")
        void deveMapearTodosOsCampos() {
            var peca = pecaCompleta();
            var dto = PecaInsumoResponseDTO.from(peca);

            assertThat(dto.id()).isEqualTo(peca.getId());
            assertThat(dto.nome()).isEqualTo("Filtro de óleo");
            assertThat(dto.descricao()).isEqualTo("Filtro original");
            assertThat(dto.precoVenda()).isEqualByComparingTo("35.00");
            assertThat(dto.precoCompra()).isEqualByComparingTo("20.00");
            assertThat(dto.quantidadePorUnidade()).isEqualTo("Unidade");
            assertThat(dto.quantidadeEstoque()).isEqualTo(10);
            assertThat(dto.quantidadeMinima()).isEqualTo(3);
            assertThat(dto.tipo()).isEqualTo(TipoPecaInsumo.PECA);
        }

        @Test
        @DisplayName("from() deve mapear tipo INSUMO")
        void deveMapearTipoInsumo() {
            var peca = PecaInsumo.builder()
                    .id(UUID.randomUUID())
                    .nome("Óleo 5W30")
                    .precoVenda(new BigDecimal("60.00"))
                    .precoCompra(new BigDecimal("40.00"))
                    .quantidadeEstoque(20)
                    .quantidadeMinima(5)
                    .tipo(TipoPecaInsumo.INSUMO)
                    .build();

            var dto = PecaInsumoResponseDTO.from(peca);

            assertThat(dto.tipo()).isEqualTo(TipoPecaInsumo.INSUMO);
        }

        @Test
        @DisplayName("from() deve calcular abaixoDoMinimo = false quando estoque >= mínimo")
        void deveRetornarAbaixoDoMinimoFalse() {
            var peca = pecaCompleta(); // estoque=10, minimo=3
            var dto = PecaInsumoResponseDTO.from(peca);

            assertThat(dto.abaixoDoMinimo()).isFalse();
        }

        @Test
        @DisplayName("from() deve calcular abaixoDoMinimo = true quando estoque < mínimo")
        void deveRetornarAbaixoDoMinimoTrue() {
            var peca = PecaInsumo.builder()
                    .id(UUID.randomUUID())
                    .nome("Vela")
                    .precoVenda(new BigDecimal("10.00"))
                    .precoCompra(new BigDecimal("6.00"))
                    .quantidadeEstoque(1)
                    .quantidadeMinima(5)
                    .tipo(TipoPecaInsumo.PECA)
                    .build();

            var dto = PecaInsumoResponseDTO.from(peca);

            assertThat(dto.abaixoDoMinimo()).isTrue();
        }

        @Test
        @DisplayName("from() deve preservar campos opcionais nulos")
        void devePreservarCamposNulos() {
            var peca = PecaInsumo.builder()
                    .id(UUID.randomUUID())
                    .nome("Vela")
                    .precoVenda(new BigDecimal("10.00"))
                    .precoCompra(new BigDecimal("6.00"))
                    .quantidadeEstoque(5)
                    .quantidadeMinima(2)
                    .tipo(TipoPecaInsumo.PECA)
                    .build();

            var dto = PecaInsumoResponseDTO.from(peca);

            assertThat(dto.descricao()).isNull();
            assertThat(dto.quantidadePorUnidade()).isNull();
        }

        @Test
        @DisplayName("from() deve refletir id nulo quando entidade ainda não foi persistida")
        void deveRefletirIdNuloAntesDePeristir() {
            var peca = PecaInsumo.builder()
                    .nome("Peca sem id")
                    .precoVenda(new BigDecimal("10.00"))
                    .precoCompra(new BigDecimal("6.00"))
                    .quantidadeEstoque(0)
                    .quantidadeMinima(0)
                    .tipo(TipoPecaInsumo.PECA)
                    .build();

            var dto = PecaInsumoResponseDTO.from(peca);

            assertThat(dto.id()).isNull();
        }
    }
}
