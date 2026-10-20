package com.fiap.tech_challenge_backend.shared.domain.valueobjects;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ValorInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Cep")
class CepTest {

    @Nested
    @DisplayName("Construcao valida")
    class Valido {

        @Test
        @DisplayName("8 digitos -> aceito")
        void oitoDigitos() {
            Cep cep = new Cep("12345678");
            assertThat(cep.valor()).isEqualTo("12345678");
            assertThat(cep.informado()).isTrue();
        }

        @Test
        @DisplayName("valor com espacos laterais e normalizado")
        void normalizado() {
            Cep cep = new Cep("  01310100  ");
            assertThat(cep.valor()).isEqualTo("01310100");
        }
    }

    @Nested
    @DisplayName("Construcao com valor ausente")
    class Ausente {

        @Test
        @DisplayName("null -> valor null, informado false")
        void valorNull() {
            Cep cep = new Cep(null);
            assertThat(cep.valor()).isNull();
            assertThat(cep.informado()).isFalse();
        }

        @Test
        @DisplayName("string em branco -> valor null, informado false")
        void stringBlank() {
            Cep cep = new Cep("   ");
            assertThat(cep.valor()).isNull();
            assertThat(cep.informado()).isFalse();
        }
    }

    @Nested
    @DisplayName("Construcao invalida lanca excecao")
    class Invalido {

        @Test
        @DisplayName("7 digitos -> invalido")
        void seteDigitos() {
            assertThatThrownBy(() -> new Cep("1234567"))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("9 digitos -> invalido")
        void noveDigitos() {
            assertThatThrownBy(() -> new Cep("123456789"))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("letras -> invalido")
        void comLetras() {
            assertThatThrownBy(() -> new Cep("1234567A"))
                    .isInstanceOf(ValorInvalidoException.class);
        }
    }

    @Nested
    @DisplayName("equals e hashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("mesmos digitos -> iguais")
        void iguais() {
            assertThat(new Cep("12345678")).isEqualTo(new Cep("12345678"));
        }

        @Test
        @DisplayName("digitos diferentes -> nao iguais")
        void diferentes() {
            assertThat(new Cep("12345678")).isNotEqualTo(new Cep("87654321"));
        }

        @Test
        @DisplayName("toString retorna o valor")
        void toStringRetornaValor() {
            assertThat(new Cep("12345678").toString()).isEqualTo("12345678");
        }
    }
}