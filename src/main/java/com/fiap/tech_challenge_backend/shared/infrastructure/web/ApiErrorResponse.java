package com.fiap.tech_challenge_backend.shared.infrastructure.web;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String code,
        String message,
        List<String> details
) {
}
