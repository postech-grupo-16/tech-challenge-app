package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServicoCatalogoRepository extends JpaRepository<ServicoCatalogoJpaEntity, UUID> {
}
