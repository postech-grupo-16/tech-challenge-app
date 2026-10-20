package com.fiap.tech_challenge_backend.shared.infrastructure.web;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller de saúde da aplicação (Health Check).
 * Contexto: shared (Infraestrutura compartilhada)
 * Camada: Infrastructure
 */
@RestController
@RequestMapping("/ping")
public class PingController {

    private final JdbcTemplate jdbcTemplate;

    public PingController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public Map<String, String> ping() {
        String dbVersion = jdbcTemplate.queryForObject("SELECT version()", String.class);
        return Map.of(
                "status", "ok",
                "db", dbVersion
        );
    }
}

