package com.fiap.tech_challenge_backend.acesso.adapters.in.web;

import com.fiap.tech_challenge_backend.acesso.application.dto.LoginRequest;
import com.fiap.tech_challenge_backend.acesso.application.exceptions.CredenciaisInvalidasException;
import com.fiap.tech_challenge_backend.acesso.application.ports.in.AutenticarUsuarioUseCase;
import com.fiap.tech_challenge_backend.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
@DisplayName("AuthController - Testes Unitários")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    private String email;
    private String senha;
    private String token;

    @BeforeEach
    void setUp() {
        email = "mecanico@oficina.com.br";
        senha = "senha123";
        token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0";
    }

    @Test
    @DisplayName("Deve fazer login com sucesso e retornar token")
    void testLoginSucesso() throws Exception {
        LoginRequest request = new LoginRequest(email, senha);

        when(autenticarUsuarioUseCase.autenticar(email, senha)).thenReturn(token);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(token));

        verify(autenticarUsuarioUseCase, times(1)).autenticar(email, senha);
    }

    @Test
    @DisplayName("Deve retornar 401 com erro padronizado quando credenciais são inválidas")
    void testLoginComCredenciaisInvalidas() throws Exception {
        LoginRequest request = new LoginRequest(email, senha);

        when(autenticarUsuarioUseCase.autenticar(email, senha))
                .thenThrow(new CredenciaisInvalidasException());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("CREDENCIAIS_INVALIDAS"))
                .andExpect(jsonPath("$.message").value("E-mail ou senha inválidos"));

        verify(autenticarUsuarioUseCase, times(1)).autenticar(email, senha);
    }
}
