package com.fiap.tech_challenge_backend.atendimento.adapters.in.web;

import com.fiap.tech_challenge_backend.atendimento.domain.exceptions.OrdemServicoStatusException;
import com.fiap.tech_challenge_backend.shared.infrastructure.web.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tratamento de exceção específico de {@code atendimento}, fora do {@code GlobalExceptionHandler}
 * compartilhado — esse arquivo não deve conhecer tipos de um contexto delimitado específico.
 * {@code OrdemServicoStatusException} continua um {@code RuntimeException} puro (sem depender de
 * {@code shared.application.exceptions.ApplicationException}, que carrega {@code HttpStatus} do
 * Spring) para não reintroduzir acoplamento a framework na camada de domínio.
 */
@RestControllerAdvice
public class OrdemServicoExceptionHandler {

    @ExceptionHandler(OrdemServicoStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleOrdemServicoStatusException(OrdemServicoStatusException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                "ORDEM_SERVICO_STATUS_INVALID",
                exception.getMessage(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }
}
