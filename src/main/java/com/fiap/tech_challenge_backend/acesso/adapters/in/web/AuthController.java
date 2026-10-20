package com.fiap.tech_challenge_backend.acesso.adapters.in.web;

import com.fiap.tech_challenge_backend.acesso.application.dto.LoginRequest;
import com.fiap.tech_challenge_backend.acesso.application.ports.in.AutenticarUsuarioUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller responsável pela autenticação de usuários.
 * Contexto Delimitado: acesso
 * Camada: Presentation
 */
@RestController
public class AuthController {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    public AuthController(AutenticarUsuarioUseCase autenticarUsuarioUseCase) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        String token = autenticarUsuarioUseCase.autenticar(request.email(), request.password());
        return ResponseEntity.ok(Map.of("accessToken", token));
    }
}
