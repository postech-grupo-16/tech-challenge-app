package com.fiap.tech_challenge_backend.acesso.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {

    boolean existsByEmail(String email);

    boolean existsByCpfCnpj(String cpfCnpj);

    Optional<UsuarioJpaEntity> findByEmail(String email);

    Optional<UsuarioJpaEntity> findByCpfCnpj(String cpfCnpj);
}
