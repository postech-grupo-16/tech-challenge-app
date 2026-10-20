package com.fiap.tech_challenge_backend.shared.domain.valueobjects;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ValorInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Email")
class EmailTest {

    @Nested
    @DisplayName("Construcao valida")
    class Valido {

        @Test
        @DisplayName("email valido -> aceito e normalizado para minusculo")
        void emailValido() {
            Email email = new Email("JOAO@EXAMPLE.COM");
            assertThat(email.valor()).isEqualTo("joao@example.com");
        }

        @Test
        @DisplayName("email com subdomain -> aceito")
        void emailComSubdomain() {
            Email email = new Email("user@mail.example.com");
            assertThat(email.valor()).isEqualTo("user@mail.example.com");
        }

        @Test
        @DisplayName("email com caracteres especiais validos -> aceito")
        void emailComCaracteresEspeciais() {
            Email email = new Email("user.name+tag@example.org");
            assertThat(email.valor()).isEqualTo("user.name+tag@example.org");
        }

        @Test
        @DisplayName("email com espacos laterais e normalizado")
        void emailComEspacos() {
            Email email = new Email("  test@test.com  ");
            assertThat(email.valor()).isEqualTo("test@test.com");
        }
    }

    @Nested
    @DisplayName("Construcao invalida lanca excecao")
    class Invalido {

        @Test
        @DisplayName("null -> invalido")
        void valorNull() {
            assertThatThrownBy(() -> new Email(null))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("blank -> invalido")
        void valorBlank() {
            assertThatThrownBy(() -> new Email("   "))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("sem arroba -> invalido")
        void semArroba() {
            assertThatThrownBy(() -> new Email("semArrobaExemplo.com"))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("sem dominio -> invalido")
        void semDominio() {
            assertThatThrownBy(() -> new Email("usuario@"))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("sem extensao de dominio -> invalido")
        void semExtensao() {
            assertThatThrownBy(() -> new Email("usuario@dominio"))
                    .isInstanceOf(ValorInvalidoException.class);
        }

        @Test
        @DisplayName("extensao de dominio com 1 letra -> invalido")
        void extensaoUmaLetra() {
            assertThatThrownBy(() -> new Email("usuario@dominio.c"))
                    .isInstanceOf(ValorInvalidoException.class);
        }
    }

    @Nested
    @DisplayName("equals e hashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("mesmo email -> iguais")
        void iguais() {
            assertThat(new Email("a@b.com")).isEqualTo(new Email("A@B.COM"));
        }

        @Test
        @DisplayName("emails diferentes -> nao iguais")
        void diferentes() {
            assertThat(new Email("a@b.com")).isNotEqualTo(new Email("c@d.com"));
        }

        @Test
        @DisplayName("hashCode consistente")
        void hashCodeConsistente() {
            assertThat(new Email("a@b.com").hashCode())
                    .isEqualTo(new Email("a@b.com").hashCode());
        }

        @Test
        @DisplayName("toString retorna o valor")
        void toStringRetornaValor() {
            assertThat(new Email("test@test.com").toString()).isEqualTo("test@test.com");
        }
    }
}