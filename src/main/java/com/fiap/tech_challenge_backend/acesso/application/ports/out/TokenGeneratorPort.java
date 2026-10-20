package com.fiap.tech_challenge_backend.acesso.application.ports.out;

import java.util.Map;

public interface TokenGeneratorPort {

    String generateToken(String subject, Map<String, Object> claims);
}
