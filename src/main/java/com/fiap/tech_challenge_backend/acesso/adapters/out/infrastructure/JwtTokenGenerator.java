package com.fiap.tech_challenge_backend.acesso.adapters.out.infrastructure;

import com.fiap.tech_challenge_backend.acesso.application.ports.out.TokenGeneratorPort;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Map;

@Component
public class JwtTokenGenerator implements TokenGeneratorPort {

    private final JwtEncoder jwtEncoder;

    public JwtTokenGenerator(@Value("${app.jwt.secret}") String jwtSecret) {
        SecretKey key = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    @Override
    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("tech-challenge-backend")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(subject);

        claims.forEach(claimsBuilder::claim);

        return jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).build(),
                        claimsBuilder.build()
                )
        ).getTokenValue();
    }
}
