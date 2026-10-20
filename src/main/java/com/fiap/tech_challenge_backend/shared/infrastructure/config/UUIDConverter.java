package com.fiap.tech_challenge_backend.shared.infrastructure.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDConverter implements Converter<String, UUID> {

    @Override
    public UUID convert(String source) {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("UUID não pode ser nulo ou vazio");
        }

        try {
            return UUID.fromString(source.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("ID fornecido '%s' não é um UUID válido. Esperado formato: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
                            source), e);
        }
    }
}
