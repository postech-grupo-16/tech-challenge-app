package com.fiap.tech_challenge_backend.shared.infrastructure.web;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiErrorResponse> handleApplicationException(
            ApplicationException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                exception.getStatus().value(),
                exception.getStatus().getReasonPhrase(),
                exception.getErrorCode(),
                exception.getMessage(),
                exception.getDetails()
        );

        return ResponseEntity.status(exception.getStatus()).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "ILLEGAL_ARGUMENT",
                exception.getMessage(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        List<String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "VALIDATION_ERROR",
                "Existem campos inválidos na requisição.",
                details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        String message;

        if (exception.getRequiredType() == UUID.class) {
            message = String.format(
                    "Parâmetro '%s' inválido. Esperado um UUID válido no formato: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx. Valor recebido: '%s'",
                    exception.getName(),
                    exception.getValue()
            );
        } else {
            message = String.format(
                    "Parâmetro '%s' não pôde ser convertido para o tipo esperado '%s'. Valor recebido: '%s'",
                    exception.getName(),
                    exception.getRequiredType().getSimpleName(),
                    exception.getValue()
            );
        }

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "INVALID_PARAMETER_TYPE",
                message,
                List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ApiErrorResponse> handleTransactionSystemException(TransactionSystemException exception) {
        log.error("Erro na transação JPA", exception);

        String message = extrairMensagemDeErroDeTransacao(exception);

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "TRANSACTION_ERROR",
                message,
                List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException exception) {
        log.error("Erro em tempo de execução", exception);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorCode = "RUNTIME_ERROR";
        String message = exception.getMessage();

        if (message != null) {
            if (message.contains("email") || message.contains("Email")) {
                status = HttpStatus.BAD_REQUEST;
                errorCode = "EMAIL_ERROR";
            } else if (message.contains("usuário") || message.contains("usuario") || message.contains("Usuário")) {
                status = HttpStatus.BAD_REQUEST;
                errorCode = "USER_ERROR";
            } else if (message.contains("transação") || message.contains("transacao")) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                errorCode = "TRANSACTION_ERROR";
            }
        }

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                errorCode,
                message != null ? message : "Ocorreu um erro inesperado.",
                List.of()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ignoredException) {
        log.error("Erro genérico não capturado", ignoredException);

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "INTERNAL_SERVER_ERROR",
                "Ocorreu um erro inesperado no servidor.",
                List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String extrairMensagemDeErroDeTransacao(TransactionSystemException exception) {
        Throwable cause = exception.getCause();

        while (cause != null) {
            String causeMensagem = cause.getMessage();
            if (causeMensagem != null) {
                if (causeMensagem.contains("NOT NULL constraint failed")) {
                    return "Erro ao salvar: um campo obrigatório não foi preenchido.";
                }
                if (causeMensagem.contains("UNIQUE constraint failed")) {
                    return "Erro ao salvar: um valor único foi violado. Este registro já existe.";
                }
                if (causeMensagem.contains("FOREIGN KEY constraint failed")) {
                    return "Erro ao salvar: referência inválida para dados relacionados.";
                }
                if (causeMensagem.contains("CHECK constraint failed")) {
                    return "Erro ao salvar: um valor não atende aos critérios válidos.";
                }
                return causeMensagem;
            }
            cause = cause.getCause();
        }

        return "Erro ao processar a transação no banco de dados. Por favor, tente novamente.";
    }
}
