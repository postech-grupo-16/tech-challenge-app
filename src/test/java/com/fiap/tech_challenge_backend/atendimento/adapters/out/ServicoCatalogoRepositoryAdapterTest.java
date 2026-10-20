package com.fiap.tech_challenge_backend.atendimento.adapters.out;

import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.ServicoCatalogoJpaEntity;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.ServicoCatalogoRepository;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.ServicoCatalogo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ServicoCatalogoRepositoryAdapter")
class ServicoCatalogoRepositoryAdapterTest {

    private ServicoCatalogoRepositoryAdapter adapter;

    @Mock
    private ServicoCatalogoRepository repository;

    private ServicoCatalogoJpaEntity servicoCatalogo;
    private UUID servicoId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new ServicoCatalogoRepositoryAdapter(repository);

        servicoId = UUID.randomUUID();
        servicoCatalogo = ServicoCatalogoJpaEntity.builder()
                .id(servicoId)
                .nome("Troca de Óleo")
                .descricao("Troca de óleo do motor com filtro")
                .precoMaoDeObra(BigDecimal.valueOf(80.00))
                .categoria("PREVENTIVA")
                .build();
    }

    @Test
    @DisplayName("Deve buscar serviço do catálogo por id")
    void testBuscarPorId() {
        when(repository.findById(servicoId)).thenReturn(Optional.of(servicoCatalogo));

        Optional<ServicoCatalogo> resultado = adapter.buscarPorId(servicoId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(servicoId);
        assertThat(resultado.get().getNome()).isEqualTo("Troca de Óleo");
        assertThat(resultado.get().getCategoria()).isEqualTo("PREVENTIVA");
        verify(repository, times(1)).findById(servicoId);
    }

    @Test
    @DisplayName("Deve retornar vazio quando serviço não existe")
    void testBuscarPorIdNaoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        Optional<ServicoCatalogo> resultado = adapter.buscarPorId(idInexistente);

        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Deve buscar serviço de diagnóstico")
    void testBuscarServicodDiagnostico() {
        ServicoCatalogoJpaEntity diagnostico = ServicoCatalogoJpaEntity.builder()
                .id(servicoId)
                .nome("Diagnóstico Eletrônico")
                .descricao("Diagnóstico completo do veículo")
                .precoMaoDeObra(BigDecimal.valueOf(150.00))
                .categoria("DIAGNOSTICO")
                .build();

        when(repository.findById(servicoId)).thenReturn(Optional.of(diagnostico));

        Optional<ServicoCatalogo> resultado = adapter.buscarPorId(servicoId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCategoria()).isEqualTo("DIAGNOSTICO");
        verify(repository, times(1)).findById(servicoId);
    }

    @Test
    @DisplayName("Deve buscar serviço corretivo")
    void testBuscarServicoCorretivo() {
        ServicoCatalogoJpaEntity corretivo = ServicoCatalogoJpaEntity.builder()
                .id(servicoId)
                .nome("Reparo de Freios")
                .descricao("Reparo completo do sistema de freios")
                .precoMaoDeObra(BigDecimal.valueOf(200.00))
                .categoria("CORRETIVA")
                .build();

        when(repository.findById(servicoId)).thenReturn(Optional.of(corretivo));

        Optional<ServicoCatalogo> resultado = adapter.buscarPorId(servicoId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCategoria()).isEqualTo("CORRETIVA");
        assertThat(resultado.get().getPrecoMaoDeObra()).isEqualByComparingTo(BigDecimal.valueOf(200.00));
        verify(repository, times(1)).findById(servicoId);
    }

    @Test
    @DisplayName("Deve buscar serviço de garantia")
    void testBuscarServicoGarantia() {
        ServicoCatalogoJpaEntity garantia = ServicoCatalogoJpaEntity.builder()
                .id(servicoId)
                .nome("Revisão de Garantia")
                .descricao("Revisão coberta pela garantia")
                .precoMaoDeObra(BigDecimal.ZERO)
                .categoria("GARANTIA")
                .build();

        when(repository.findById(servicoId)).thenReturn(Optional.of(garantia));

        Optional<ServicoCatalogo> resultado = adapter.buscarPorId(servicoId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCategoria()).isEqualTo("GARANTIA");
        verify(repository, times(1)).findById(servicoId);
    }

    @Test
    @DisplayName("Deve validar preço de mão de obra do serviço")
    void testBuscarServicoComPreco() {
        when(repository.findById(servicoId)).thenReturn(Optional.of(servicoCatalogo));

        Optional<ServicoCatalogo> resultado = adapter.buscarPorId(servicoId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getPrecoMaoDeObra()).isEqualByComparingTo(BigDecimal.valueOf(80.00));
        verify(repository, times(1)).findById(servicoId);
    }

    @Test
    @DisplayName("Deve buscar serviço com descrição completa")
    void testBuscarServicoComDescricao() {
        when(repository.findById(servicoId)).thenReturn(Optional.of(servicoCatalogo));

        Optional<ServicoCatalogo> resultado = adapter.buscarPorId(servicoId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getDescricao()).isEqualTo("Troca de óleo do motor com filtro");
        verify(repository, times(1)).findById(servicoId);
    }
}
