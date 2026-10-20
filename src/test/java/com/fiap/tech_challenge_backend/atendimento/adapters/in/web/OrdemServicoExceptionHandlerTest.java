package com.fiap.tech_challenge_backend.atendimento.adapters.in.web;

import com.fiap.tech_challenge_backend.atendimento.domain.exceptions.OrdemServicoStatusException;
import com.fiap.tech_challenge_backend.shared.infrastructure.web.ApiErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrdemServicoExceptionHandler")
class OrdemServicoExceptionHandlerTest {

    private OrdemServicoExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new OrdemServicoExceptionHandler();
    }

    @Test
    @DisplayName("retorna 422 com code ORDEM_SERVICO_STATUS_INVALID")
    void retorna422() {
        OrdemServicoStatusException ex = new OrdemServicoStatusException("transicao invalida");

        ResponseEntity<ApiErrorResponse> response = handler.handleOrdemServicoStatusException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(422);
        assertThat(response.getBody().code()).isEqualTo("ORDEM_SERVICO_STATUS_INVALID");
        assertThat(response.getBody().message()).isEqualTo("transicao invalida");
        assertThat(response.getBody().details()).isEmpty();
    }
}
