package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.ServicoCatalogo;

public final class ServicoCatalogoMapper {

    private ServicoCatalogoMapper() {
    }

    public static ServicoCatalogo toDomain(ServicoCatalogoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ServicoCatalogo.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .precoMaoDeObra(entity.getPrecoMaoDeObra())
                .categoria(entity.getCategoria())
                .build();
    }

    public static ServicoCatalogoJpaEntity toEntity(ServicoCatalogo domain) {
        if (domain == null) {
            return null;
        }
        return ServicoCatalogoJpaEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .precoMaoDeObra(domain.getPrecoMaoDeObra())
                .categoria(domain.getCategoria())
                .build();
    }
}
