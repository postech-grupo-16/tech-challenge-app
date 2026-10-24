package com.fiap.tech_challenge_backend.shared.infrastructure.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Correlação entre requisições: cada requisição recebe um {@code requestId}
 * (reaproveitado do header {@code X-Request-Id}/{@code X-Correlation-Id}, ou gerado),
 * colocado no MDC — aparece em todo log JSON e é enviado ao New Relic — e devolvido
 * no header de resposta {@code X-Request-Id}.
 * Contexto: shared (Infraestrutura compartilhada)
 * Camada: Infrastructure
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String MDC_KEY = "requestId";

    private static final int MAX_LENGTH = 128;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestId = resolverRequestId(request);
        MDC.put(MDC_KEY, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }

    private String resolverRequestId(HttpServletRequest request) {
        String recebido = request.getHeader(REQUEST_ID_HEADER);
        if (!StringUtils.hasText(recebido)) {
            recebido = request.getHeader(CORRELATION_ID_HEADER);
        }
        // Valor vindo de fora vai para logs: descarta se vazio ou grande demais
        if (!StringUtils.hasText(recebido) || recebido.length() > MAX_LENGTH) {
            return UUID.randomUUID().toString();
        }
        return recebido.trim();
    }
}
