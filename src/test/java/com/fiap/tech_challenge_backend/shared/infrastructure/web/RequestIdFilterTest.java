package com.fiap.tech_challenge_backend.shared.infrastructure.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RequestIdFilter")
class RequestIdFilterTest {

    private RequestIdFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new RequestIdFilter();
        request = new MockHttpServletRequest("GET", "/api/v1/ordens-servico/listar-os");
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("Reaproveita o X-Request-Id recebido e o coloca no MDC durante a requisição")
    void deveReaproveitarRequestIdRecebido() throws Exception {
        request.addHeader(RequestIdFilter.REQUEST_ID_HEADER, "abc-123");
        AtomicReference<String> noMdc = new AtomicReference<>();

        filter.doFilter(request, response, (req, res) -> noMdc.set(MDC.get(RequestIdFilter.MDC_KEY)));

        assertThat(noMdc.get()).isEqualTo("abc-123");
        assertThat(response.getHeader(RequestIdFilter.REQUEST_ID_HEADER)).isEqualTo("abc-123");
    }

    @Test
    @DisplayName("Usa o X-Correlation-Id quando não há X-Request-Id")
    void deveUsarCorrelationId() throws Exception {
        request.addHeader(RequestIdFilter.CORRELATION_ID_HEADER, "corr-456");

        filter.doFilter(request, response, (req, res) -> { });

        assertThat(response.getHeader(RequestIdFilter.REQUEST_ID_HEADER)).isEqualTo("corr-456");
    }

    @Test
    @DisplayName("Gera um UUID quando nenhum header de correlação é enviado")
    void deveGerarRequestIdQuandoAusente() throws Exception {
        filter.doFilter(request, response, (req, res) -> { });

        String gerado = response.getHeader(RequestIdFilter.REQUEST_ID_HEADER);
        assertThatCode(() -> UUID.fromString(gerado)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Descarta header grande demais e gera um novo UUID")
    void deveDescartarHeaderGrandeDemais() throws Exception {
        request.addHeader(RequestIdFilter.REQUEST_ID_HEADER, "x".repeat(200));

        filter.doFilter(request, response, (req, res) -> { });

        String gerado = response.getHeader(RequestIdFilter.REQUEST_ID_HEADER);
        assertThatCode(() -> UUID.fromString(gerado)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Limpa o MDC ao final, mesmo quando a requisição lança exceção")
    void deveLimparMdcAoFinal() {
        assertThatThrownBy(() -> filter.doFilter(request, response, (req, res) -> {
            throw new IllegalStateException("falha");
        })).isInstanceOf(IllegalStateException.class);

        assertThat(MDC.get(RequestIdFilter.MDC_KEY)).isNull();
    }
}
