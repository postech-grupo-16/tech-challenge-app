package com.fiap.tech_challenge_backend.shared.domain.valueobjects;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ValorInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Telefone")
class TelefoneTest {

    @Nested
    @DisplayName("Construcao valida")
    class Valido {

        @Test
        @DisplayName("10 digitos (fixo) -> aceito")
        void dezDigitos() {
            Telefone tel = new Telefone("1134567890");
            assertThat(tel.valor()).isEqualTo("1134567890");
            assertThat(tel.informado()).isTrue();
        }

        @Test
        @DisplayName("11 digitos (celular) -> aceito")
        void onzeDigitos() {
            Telefone tel = new Telefone("11987654321");
            assertThat(tel.valor()).isEqualTo("11987654321");
            assertThat(tel.informado()).isTrue();
        }

        @Test
        @DisplayName("valor com espacos laterais e normalizado")
        void comEspacos() {
            Telefone tel = new Telefone("  11987654321  ");
            assertThat(tel.valor()).isEqualTo("11987654321");
        }
    }

    @Nested
    @DisplayName("Construcao com valor ausente")
    class Ausente {

        @Test
        @DisplayName("null -> valor null, informado false")
        void valorNull() {
            Telefone tel = new Telefone(null);
            assertThat(tel.valor()).isNull();
            assertThat(tel.informado()).isFalse();
        }

        @Test
        @DisplayName("blank -> valor null, informado false")
        void valorBlank() {
            Telefone tel = new Telefone("  ");
            assertThat(tel.valor()).isNull();
            assertThat(tel.informado()).isFalse();
        }
    }

    @Nested
    @DisplayName("Construcao invalida lanca excecao")
    class Invalido {

        @Test
        @DisplayName("9 digitos -> invalido")
        void noveDigitos() {
            assertThatThrownBy(() -> new Telefone("119876543"))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("12 digitos -> invalido")
        void dozeDigitos() {
            assertThatThrownBy(() -> new Telefone("119876543210"))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("letras -> invalido")
        void comLetras() {
            assertThatThrownBy(() -> new Telefone("1198765432A"))
                    .isInstanceOf(ValorInvalidoException.class);
        }
    }

    @Nested
    @DisplayName("equals e hashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("mesmo numero -> iguais")
        void iguais() {
            assertThat(new Telefone("11987654321")).isEqualTo(new Telefone("11987654321"));
        }

        @Test
        @DisplayName("numeros diferentes -> nao iguais")
        void diferentes() {
            assertThat(new Telefone("11987654321")).isNotEqualTo(new Telefone("11912345678"));
        }

        @Test
        @DisplayName("hashCode consistente")
        void hashCodeConsistente() {
            assertThat(new Telefone("11987654321").hashCode())
                    .isEqualTo(new Telefone("11987654321").hashCode());
        }

        @Test
        @DisplayName("toString retorna o valor")
        void toStringRetornaValor() {
            assertThat(new Telefone("11987654321").toString()).isEqualTo("11987654321");
        }
    }
}