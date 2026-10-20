package com.fiap.tech_challenge_backend.estoque.adapters.out.persistence;

import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MovimentacaoEstoqueJpaEntity.prePersist")
class MovimentacaoEstoqueJpaEntityTest {

    @Test
    @DisplayName("deve preencher criadoEm ao chamar prePersist")
    void devePreencherCriadoEm() {
        var mov = MovimentacaoEstoqueJpaEntity.builder()
                .tipoMovimentacao(TipoMovimentacao.ENTRADA)
                .quantidade(5)
                .build();

        mov.prePersist();

        assertThat(mov.getCriadoEm()).isNotNull();
    }

    @Test
    @DisplayName("criadoEm deve ser próximo ao momento atual")
    void criadoEmDeveSerAtual() {
        var mov = MovimentacaoEstoqueJpaEntity.builder()
                .tipoMovimentacao(TipoMovimentacao.SAIDA)
                .quantidade(2)
                .build();

        var antes = LocalDateTime.now().minusSeconds(1);
        mov.prePersist();
        var depois = LocalDateTime.now().plusSeconds(1);

        assertThat(mov.getCriadoEm()).isAfter(antes).isBefore(depois);
    }
}
