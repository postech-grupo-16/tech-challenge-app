package com.fiap.tech_challenge_backend.shared.infrastructure.web;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    private static class TesteApplicationException extends ApplicationException {
        TesteApplicationException(HttpStatus status, String code, String message) {
            super(status, code, message);
        }
        TesteApplicationException(HttpStatus status, String code, String message, List<String> details) {
            super(status, code, message, details);
        }
    }

    // ─── ApplicationException ────────────────────────────────────────────────

    @Nested
    @DisplayName("ApplicationException")
    class HandleApplicationException {

        @Test
        @DisplayName("retorna status e errorCode da excecao")
        void retornaStatusECodigo() {
            TesteApplicationException ex = new TesteApplicationException(
                    HttpStatus.CONFLICT, "CONFLITO", "recurso duplicado");

            ResponseEntity<ApiErrorResponse> response = handler.handleApplicationException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(409);
            assertThat(response.getBody().code()).isEqualTo("CONFLITO");
            assertThat(response.getBody().message()).isEqualTo("recurso duplicado");
            assertThat(response.getBody().details()).isEmpty();
        }

        @Test
        @DisplayName("propaga details quando presentes")
        void propagaDetails() {
            TesteApplicationException ex = new TesteApplicationException(
                    HttpStatus.BAD_REQUEST, "VALIDACAO", "campos invalidos",
                    List.of("campo1: obrigatorio", "campo2: invalido"));

            ResponseEntity<ApiErrorResponse> response = handler.handleApplicationException(ex);

            assertThat(response.getBody().details())
                    .containsExactly("campo1: obrigatorio", "campo2: invalido");
        }
    }

    // ─── IllegalArgumentException ─────────────────────────────────────────────

    @Nested
    @DisplayName("IllegalArgumentException")
    class HandleIllegalArgumentException {

        @Test
        @DisplayName("retorna 400 com code ILLEGAL_ARGUMENT")
        void retorna400() {
            IllegalArgumentException ex = new IllegalArgumentException("valor invalido");

            ResponseEntity<ApiErrorResponse> response = handler.handleIllegalArgumentException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().code()).isEqualTo("ILLEGAL_ARGUMENT");
            assertThat(response.getBody().message()).isEqualTo("valor invalido");
        }
    }

    // ─── MethodArgumentNotValidException ──────────────────────────────────────

    @Nested
    @DisplayName("MethodArgumentNotValidException")
    class HandleValidationException {

        @Test
        @DisplayName("retorna 400 com field errors mapeados em details")
        void retornaFieldErrors() {
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fe1 = new FieldError("obj", "nome", "nao deve ser nulo");
            FieldError fe2 = new FieldError("obj", "email", "formato invalido");
            when(ex.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fe1, fe2));

            ResponseEntity<ApiErrorResponse> response = handler.handleValidationException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().code()).isEqualTo("VALIDATION_ERROR");
            assertThat(response.getBody().message()).isEqualTo("Existem campos inválidos na requisição.");
            assertThat(response.getBody().details())
                    .containsExactly("nome: nao deve ser nulo", "email: formato invalido");
        }

        @Test
        @DisplayName("retorna details vazio quando sem field errors")
        void retornaDetailsVazio() {
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            when(ex.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of());

            ResponseEntity<ApiErrorResponse> response = handler.handleValidationException(ex);

            assertThat(response.getBody().details()).isEmpty();
        }
    }

    // ─── MethodArgumentTypeMismatchException ──────────────────────────────────

    @Nested
    @DisplayName("MethodArgumentTypeMismatchException")
    class HandleTypeMismatch {

        @Test
        @DisplayName("gera mensagem especifica para parametro do tipo UUID")
        void mensagemParaUUID() {
            MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
            when(ex.getRequiredType()).thenAnswer(inv -> UUID.class);
            when(ex.getName()).thenReturn("clienteId");
            when(ex.getValue()).thenReturn("nao-e-uuid");

            ResponseEntity<ApiErrorResponse> response = handler.handleMethodArgumentTypeMismatch(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().code()).isEqualTo("INVALID_PARAMETER_TYPE");
            assertThat(response.getBody().message()).contains("clienteId");
            assertThat(response.getBody().message()).contains("UUID");
            assertThat(response.getBody().message()).contains("nao-e-uuid");
        }

        @Test
        @DisplayName("gera mensagem generica para parametro de tipo nao-UUID")
        void mensagemGenerica() {
            MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
            when(ex.getRequiredType()).thenAnswer(inv -> Long.class);
            when(ex.getName()).thenReturn("id");
            when(ex.getValue()).thenReturn("abc");

            ResponseEntity<ApiErrorResponse> response = handler.handleMethodArgumentTypeMismatch(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().message()).contains("id");
            assertThat(response.getBody().message()).contains("Long");
            assertThat(response.getBody().message()).contains("abc");
        }
    }

    // ─── TransactionSystemException ───────────────────────────────────────────

    @Nested
    @DisplayName("TransactionSystemException")
    class HandleTransactionSystemException {

        @Test
        @DisplayName("NOT NULL constraint -> mensagem de campo obrigatorio")
        void notNullConstraint() {
            TransactionSystemException ex = new TransactionSystemException("tx",
                    new RuntimeException("NOT NULL constraint failed"));

            ResponseEntity<ApiErrorResponse> response = handler.handleTransactionSystemException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().code()).isEqualTo("TRANSACTION_ERROR");
            assertThat(response.getBody().message())
                    .isEqualTo("Erro ao salvar: um campo obrigatório não foi preenchido.");
        }

        @Test
        @DisplayName("UNIQUE constraint -> mensagem de registro duplicado")
        void uniqueConstraint() {
            TransactionSystemException ex = new TransactionSystemException("tx",
                    new RuntimeException("UNIQUE constraint failed: tabela.coluna"));

            ResponseEntity<ApiErrorResponse> response = handler.handleTransactionSystemException(ex);

            assertThat(response.getBody().message())
                    .isEqualTo("Erro ao salvar: um valor único foi violado. Este registro já existe.");
        }

        @Test
        @DisplayName("FOREIGN KEY constraint -> mensagem de referencia invalida")
        void foreignKeyConstraint() {
            TransactionSystemException ex = new TransactionSystemException("tx",
                    new RuntimeException("FOREIGN KEY constraint failed"));

            ResponseEntity<ApiErrorResponse> response = handler.handleTransactionSystemException(ex);

            assertThat(response.getBody().message())
                    .isEqualTo("Erro ao salvar: referência inválida para dados relacionados.");
        }

        @Test
        @DisplayName("CHECK constraint -> mensagem de criterio nao atendido")
        void checkConstraint() {
            TransactionSystemException ex = new TransactionSystemException("tx",
                    new RuntimeException("CHECK constraint failed: regra"));

            ResponseEntity<ApiErrorResponse> response = handler.handleTransactionSystemException(ex);

            assertThat(response.getBody().message())
                    .isEqualTo("Erro ao salvar: um valor não atende aos critérios válidos.");
        }

        @Test
        @DisplayName("causa com mensagem generica e retornada diretamente")
        void causaGenerica() {
            TransactionSystemException ex = new TransactionSystemException("tx",
                    new RuntimeException("erro generico de banco"));

            ResponseEntity<ApiErrorResponse> response = handler.handleTransactionSystemException(ex);

            assertThat(response.getBody().message()).isEqualTo("erro generico de banco");
        }

        @Test
        @DisplayName("sem causa retorna mensagem de fallback")
        void semCausa() {
            TransactionSystemException ex = new TransactionSystemException("sem causa");

            ResponseEntity<ApiErrorResponse> response = handler.handleTransactionSystemException(ex);

            assertThat(response.getBody().message())
                    .isEqualTo("Erro ao processar a transação no banco de dados. Por favor, tente novamente.");
        }

        @Test
        @DisplayName("causa com message null percorre cadeia ate encontrar mensagem")
        void causaComMessageNullPercorreCadeia() {
            RuntimeException innerCause = new RuntimeException("UNIQUE constraint failed: duplicado");
            RuntimeException outerCause = new RuntimeException((String) null, innerCause);
            TransactionSystemException ex = new TransactionSystemException("tx", outerCause);

            ResponseEntity<ApiErrorResponse> response = handler.handleTransactionSystemException(ex);

            assertThat(response.getBody().message())
                    .isEqualTo("Erro ao salvar: um valor único foi violado. Este registro já existe.");
        }
    }

    // ─── RuntimeException ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("RuntimeException")
    class HandleRuntimeException {

        @Test
        @DisplayName("mensagem com 'email' resulta em 400 e EMAIL_ERROR")
        void mensagemComEmailMinusculo() {
            RuntimeException ex = new RuntimeException("email ja cadastrado");

            ResponseEntity<ApiErrorResponse> response = handler.handleRuntimeException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().code()).isEqualTo("EMAIL_ERROR");
        }

        @Test
        @DisplayName("mensagem com 'Email' (maiusculo) resulta em 400 e EMAIL_ERROR")
        void mensagemComEmailMaiusculo() {
            RuntimeException ex = new RuntimeException("Email invalido informado");

            ResponseEntity<ApiErrorResponse> response = handler.handleRuntimeException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().code()).isEqualTo("EMAIL_ERROR");
        }

        @Test
        @DisplayName("mensagem com 'usuario' resulta em 400 e USER_ERROR")
        void mensagemComUsuario() {
            RuntimeException ex = new RuntimeException("usuario nao encontrado");

            ResponseEntity<ApiErrorResponse> response = handler.handleRuntimeException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().code()).isEqualTo("USER_ERROR");
        }

        @Test
        @DisplayName("mensagem com 'transacao' resulta em 500 e TRANSACTION_ERROR")
        void mensagemComTransacao() {
            RuntimeException ex = new RuntimeException("erro na transacao do banco");

            ResponseEntity<ApiErrorResponse> response = handler.handleRuntimeException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().code()).isEqualTo("TRANSACTION_ERROR");
        }

        @Test
        @DisplayName("mensagem generica resulta em 500 e RUNTIME_ERROR")
        void mensagemGenerica() {
            RuntimeException ex = new RuntimeException("falha inesperada");

            ResponseEntity<ApiErrorResponse> response = handler.handleRuntimeException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().code()).isEqualTo("RUNTIME_ERROR");
            assertThat(response.getBody().message()).isEqualTo("falha inesperada");
        }

        @Test
        @DisplayName("mensagem null resulta em 500 com texto padrao")
        void mensagemNull() {
            RuntimeException ex = new RuntimeException((String) null);

            ResponseEntity<ApiErrorResponse> response = handler.handleRuntimeException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().code()).isEqualTo("RUNTIME_ERROR");
            assertThat(response.getBody().message()).isEqualTo("Ocorreu um erro inesperado.");
        }
    }

    // ─── Exception generica ───────────────────────────────────────────────────

    @Nested
    @DisplayName("Exception generica")
    class HandleGenericException {

        @Test
        @DisplayName("retorna 500 com code INTERNAL_SERVER_ERROR e mensagem padrao")
        void retorna500() throws Exception {
            Exception ex = new Exception("erro nao capturado");

            ResponseEntity<ApiErrorResponse> response = handler.handleGenericException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().status()).isEqualTo(500);
            assertThat(response.getBody().code()).isEqualTo("INTERNAL_SERVER_ERROR");
            assertThat(response.getBody().message()).isEqualTo("Ocorreu um erro inesperado no servidor.");
            assertThat(response.getBody().details()).isEmpty();
        }
    }
}