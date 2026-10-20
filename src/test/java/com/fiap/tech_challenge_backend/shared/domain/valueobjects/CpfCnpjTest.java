package com.fiap.tech_challenge_backend.shared.domain.valueobjects;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ValorInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CpfCnpj")
class CpfCnpjTest {

    @Nested
    @DisplayName("CPF valido (11 digitos)")
    class CpfValido {

        @Test
        @DisplayName("11 digitos -> aceito, isCpf true")
        void onzeDigitos() {
            CpfCnpj cpf = new CpfCnpj("12345678901");
            assertThat(cpf.valor()).isEqualTo("12345678901");
            assertThat(cpf.isCpf()).isTrue();
            assertThat(cpf.isCnpj()).isFalse();
        }

        @Test
        @DisplayName("valor com espacos e normalizado")
        void comEspacos() {
            CpfCnpj cpf = new CpfCnpj("  12345678901  ");
            assertThat(cpf.valor()).isEqualTo("12345678901");
        }
    }

    @Nested
    @DisplayName("CNPJ valido (14 alfanumerico)")
    class CnpjValido {

        @Test
        @DisplayName("14 digitos numericos -> aceito, isCnpj true")
        void quatorzeDigitos() {
            CpfCnpj cnpj = new CpfCnpj("12345678000195");
            assertThat(cnpj.valor()).isEqualTo("12345678000195");
            assertThat(cnpj.isCnpj()).isTrue();
            assertThat(cnpj.isCpf()).isFalse();
        }

        @Test
        @DisplayName("14 caracteres alfanumericos minusculos -> normalizado para maiusculo")
        void alfanumericoMinusculo() {
            CpfCnpj cnpj = new CpfCnpj("12abc678000195");
            assertThat(cnpj.valor()).isEqualTo("12ABC678000195");
            assertThat(cnpj.isCnpj()).isTrue();
        }
    }

    @Nested
    @DisplayName("Construcao invalida lanca excecao")
    class Invalido {

        @Test
        @DisplayName("null -> invalido")
        void valorNull() {
            assertThatThrownBy(() -> new CpfCnpj(null))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("blank -> invalido")
        void valorBlank() {
            assertThatThrownBy(() -> new CpfCnpj("  "))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("10 digitos -> invalido")
        void dezDigitos() {
            assertThatThrownBy(() -> new CpfCnpj("1234567890"))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("12 digitos -> invalido")
        void dozeDigitos() {
            assertThatThrownBy(() -> new CpfCnpj("123456789012"))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("13 digitos -> invalido")
        void trezeDigitos() {
            assertThatThrownBy(() -> new CpfCnpj("1234567890123"))
                    .isInstanceOf(ValorInvalidoException.class);
        }
    }

    @Nested
    @DisplayName("equals e hashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("mesmo valor -> iguais")
        void iguais() {
            assertThat(new CpfCnpj("12345678901")).isEqualTo(new CpfCnpj("12345678901"));
        }

        @Test
        @DisplayName("valores diferentes -> nao iguais")
        void diferentes() {
            assertThat(new CpfCnpj("12345678901")).isNotEqualTo(new CpfCnpj("10987654321"));
        }

        @Test
        @DisplayName("hashCode consistente")
        void hashCodeConsistente() {
            assertThat(new CpfCnpj("12345678901").hashCode())
                    .isEqualTo(new CpfCnpj("12345678901").hashCode());
        }

        @Test
        @DisplayName("toString retorna o valor")
        void toStringRetornaValor() {
            assertThat(new CpfCnpj("12345678901").toString()).isEqualTo("12345678901");
        }
    }
}