package com.fiap.tech_challenge_backend.acesso.adapters.in.web;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller responsável por retornar dados do usuário autenticado.
 * Contexto Delimitado: acesso
 * Camada: Presentation
 */
@RestController
@SecurityRequirement(name = "bearerAuth")
public class MeController {

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        return Map.of(
                "name", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );
    }
}
