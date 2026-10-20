package com.fiap.tech_challenge_backend.shared.domain.valueobjects;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ValorInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Placa")
class PlacaTest {

    @Nested
    @DisplayName("Formato antigo (ABC1234)")
    class FormatoAntigo {

        @Test
        @DisplayName("formato antigo -> aceito, isFormatoAntigo true")
        void formatoAntigo() {
            Placa placa = new Placa("ABC1234");
            assertThat(placa.valor()).isEqualTo("ABC1234");
            assertThat(placa.isFormatoAntigo()).isTrue();
            assertThat(placa.isFormatoMercosul()).isFalse();
        }

        @Test
        @DisplayName("minusculo -> normalizado para maiusculo")
        void minusculo() {
            Placa placa = new Placa("abc1234");
            assertThat(placa.valor()).isEqualTo("ABC1234");
            assertThat(placa.isFormatoAntigo()).isTrue();
        }

        @Test
        @DisplayName("com espacos laterais -> normalizado")
        void comEspacos() {
            Placa placa = new Placa("  ABC1234  ");
            assertThat(placa.valor()).isEqualTo("ABC1234");
        }
    }

    @Nested
    @DisplayName("Formato Mercosul (ABC1D23)")
    class FormatoMercosul {

        @Test
        @DisplayName("formato Mercosul -> aceito, isFormatoMercosul true")
        void formatoMercosul() {
            Placa placa = new Placa("ABC1D23");
            assertThat(placa.valor()).isEqualTo("ABC1D23");
            assertThat(placa.isFormatoMercosul()).isTrue();
            assertThat(placa.isFormatoAntigo()).isFalse();
        }

        @Test
        @DisplayName("minusculo -> normalizado para maiusculo")
        void minusculo() {
            Placa placa = new Placa("abc1d23");
            assertThat(placa.valor()).isEqualTo("ABC1D23");
            assertThat(placa.isFormatoMercosul()).isTrue();
        }
    }

    @Nested
    @DisplayName("Construcao invalida lanca excecao")
    class Invalido {

        @Test
        @DisplayName("null -> invalido")
        void valorNull() {
            assertThatThrownBy(() -> new Placa(null))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("blank -> invalido")
        void valorBlank() {
            assertThatThrownBy(() -> new Placa("  "))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("apenas letras sem numeros -> invalido")
        void apenasLetras() {
            assertThatThrownBy(() -> new Placa("ABCDEFG"))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("apenas numeros -> invalido")
        void apenasNumeros() {
            assertThatThrownBy(() -> new Placa("1234567"))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("menos de 7 caracteres -> invalido")
        void menosDe7() {
            assertThatThrownBy(() -> new Placa("ABC123"))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("mais de 7 caracteres -> invalido")
        void maisDe7() {
            assertThatThrownBy(() -> new Placa("ABC12345"))
                    .isInstanceOf(ValorInvalidoException.class);
        }
    }

    @Nested
    @DisplayName("equals e hashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("mesma placa -> iguais")
        void iguais() {
            assertThat(new Placa("ABC1234")).isEqualTo(new Placa("ABC1234"));
        }

        @Test
        @DisplayName("placas diferentes -> nao iguais")
        void diferentes() {
            assertThat(new Placa("ABC1234")).isNotEqualTo(new Placa("XYZ9876"));
        }

        @Test
        @DisplayName("hashCode consistente")
        void hashCodeConsistente() {
            assertThat(new Placa("ABC1234").hashCode())
                    .isEqualTo(new Placa("ABC1234").hashCode());
        }

        @Test
        @DisplayName("toString retorna o valor")
        void toStringRetornaValor() {
            assertThat(new Placa("ABC1234").toString()).isEqualTo("ABC1234");
        }
    }
}