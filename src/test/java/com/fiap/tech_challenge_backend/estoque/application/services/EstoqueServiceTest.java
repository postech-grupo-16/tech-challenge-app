package com.fiap.tech_challenge_backend.estoque.application.services;

import com.fiap.tech_challenge_backend.estoque.application.dto.EntradaEstoqueRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.exceptions.PecaInsumoNaoEncontradoException;
import com.fiap.tech_challenge_backend.estoque.application.ports.out.MovimentacaoRepositoryPort;
import com.fiap.tech_challenge_backend.estoque.application.ports.out.PecaInsumoRepositoryPort;
import com.fiap.tech_challenge_backend.estoque.domain.entities.MovimentacaoEstoque;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EstoqueService")
class EstoqueServiceTest {

    @Mock private PecaInsumoRepositoryPort pecaInsumoRepository;
    @Mock private MovimentacaoRepositoryPort movimentacaoRepository;
    @InjectMocks private EstoqueService service;

    private UUID pecaId;
    private PecaInsumo peca;

    @BeforeEach
    void setUp() {
        pecaId = UUID.randomUUID();
        peca = PecaInsumo.builder()
                .id(pecaId)
                .nome("Filtro de Oleo")
                .tipo(TipoPecaInsumo.PECA)
                .precoVenda(new BigDecimal("35.00"))
                .precoCompra(new BigDecimal("20.00"))
                .quantidadeEstoque(10)
                .quantidadeMinima(3)
                .build();
    }

    // ─────────────────────────────────────────────
    // cadastrar
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("cadastrar")
    class Cadastrar {

        @Test
        @DisplayName("deve salvar e retornar DTO com dados do request")
        void deveSalvarERetornarDTO() {
            var request = new PecaInsumoRequestDTO(
                    "Correia Dentada", "Correia do motor",
                    new BigDecimal("85.00"), new BigDecimal("50.00"),
                    "Unidade", 20, 5, TipoPecaInsumo.PECA);

            var salva = PecaInsumo.builder()
                    .id(UUID.randomUUID())
                    .nome("Correia Dentada")
                    .precoVenda(new BigDecimal("85.00"))
                    .precoCompra(new BigDecimal("50.00"))
                    .quantidadeEstoque(20)
                    .quantidadeMinima(5)
                    .tipo(TipoPecaInsumo.PECA)
                    .build();
            when(pecaInsumoRepository.salvar(any())).thenReturn(salva);

            var dto = service.cadastrar(request);

            assertThat(dto.nome()).isEqualTo("Correia Dentada");
            assertThat(dto.quantidadeEstoque()).isEqualTo(20);
            verify(pecaInsumoRepository).salvar(any(PecaInsumo.class));
            verifyNoInteractions(movimentacaoRepository);
        }
    }

    // ─────────────────────────────────────────────
    // atualizar
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("atualizar")
    class Atualizar {

        private PecaInsumoRequestDTO requestComNovoEstoque(int estoque) {
            return new PecaInsumoRequestDTO(
                    "Filtro Premium", null,
                    new BigDecimal("40.00"), new BigDecimal("22.00"),
                    null, estoque, 3, TipoPecaInsumo.PECA);
        }

        @Test
        @DisplayName("deve atualizar campos e gerar movimentacao AJUSTE quando estoque aumenta")
        void deveAtualizarEGerarAjusteQuandoAumenta() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

            var captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);

            service.atualizar(pecaId, requestComNovoEstoque(20)); // era 10, vai para 20

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(20);
            verify(movimentacaoRepository).salvar(captor.capture());
            assertThat(captor.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.AJUSTE);
            assertThat(captor.getValue().getQuantidade()).isEqualTo(10);
        }

        @Test
        @DisplayName("deve atualizar campos e gerar movimentacao AJUSTE quando estoque diminui")
        void deveAtualizarEGerarAjusteQuandoDiminui() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

            var captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);

            service.atualizar(pecaId, requestComNovoEstoque(4)); // era 10, vai para 4

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(4);
            verify(movimentacaoRepository).salvar(captor.capture());
            assertThat(captor.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.AJUSTE);
            assertThat(captor.getValue().getQuantidade()).isEqualTo(6);
        }

        @Test
        @DisplayName("nao deve gerar movimentacao quando estoque nao muda")
        void naoDeveGerarAjusteQuandoEstoqueIgual() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

            service.atualizar(pecaId, requestComNovoEstoque(10)); // mesmo valor

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(10);
            verifyNoInteractions(movimentacaoRepository);
        }

        @Test
        @DisplayName("deve lancar PecaInsumoNaoEncontradoException quando peca nao existe")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.atualizar(pecaId, requestComNovoEstoque(5)))
                    .isInstanceOf(PecaInsumoNaoEncontradoException.class);
        }
    }

    // ─────────────────────────────────────────────
    // buscarPorId
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar DTO quando peca existe")
        void deveRetornarDTO() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.of(peca));

            var dto = service.buscarPorId(pecaId);

            assertThat(dto.id()).isEqualTo(pecaId);
            assertThat(dto.nome()).isEqualTo("Filtro de Oleo");
        }

        @Test
        @DisplayName("deve lancar PecaInsumoNaoEncontradoException quando peca nao existe")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId(pecaId))
                    .isInstanceOf(PecaInsumoNaoEncontradoException.class);
        }
    }

    // ─────────────────────────────────────────────
    // listarTodos
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("listarTodos")
    class ListarTodos {

        @Test
        @DisplayName("sem filtro de tipo deve chamar buscarTodos")
        void semFiltroDeveUsarBuscarTodos() {
            when(pecaInsumoRepository.buscarTodos()).thenReturn(List.of(peca));

            var resultado = service.listarTodos(null);

            assertThat(resultado).hasSize(1);
            verify(pecaInsumoRepository).buscarTodos();
            verify(pecaInsumoRepository, never()).buscarPorTipo(any());
        }

        @Test
        @DisplayName("com filtro de tipo deve chamar buscarPorTipo")
        void comFiltroDeveUsarBuscarPorTipo() {
            when(pecaInsumoRepository.buscarPorTipo(TipoPecaInsumo.PECA)).thenReturn(List.of(peca));

            var resultado = service.listarTodos(TipoPecaInsumo.PECA);

            assertThat(resultado).hasSize(1);
            verify(pecaInsumoRepository).buscarPorTipo(TipoPecaInsumo.PECA);
            verify(pecaInsumoRepository, never()).buscarTodos();
        }
    }

    // ─────────────────────────────────────────────
    // listarAbaixoDoMinimo
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("listarAbaixoDoMinimo")
    class ListarAbaixoDoMinimo {

        @Test
        @DisplayName("deve retornar DTOs mapeados das pecas abaixo do minimo")
        void deveRetornarDTOs() {
            when(pecaInsumoRepository.buscarAbaixoDoMinimo()).thenReturn(List.of(peca));

            var resultado = service.listarAbaixoDoMinimo();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).nome()).isEqualTo("Filtro de Oleo");
        }

        @Test
        @DisplayName("deve retornar lista vazia quando nenhuma peca abaixo do minimo")
        void deveRetornarListaVazia() {
            when(pecaInsumoRepository.buscarAbaixoDoMinimo()).thenReturn(List.of());

            assertThat(service.listarAbaixoDoMinimo()).isEmpty();
        }
    }

    // ─────────────────────────────────────────────
    // remover
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("remover")
    class Remover {

        @Test
        @DisplayName("deve aplicar soft delete e salvar peca")
        void deveAplicarSoftDelete() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

            service.remover(pecaId);

            assertThat(peca.getDeletedAt()).isNotNull();
            verify(pecaInsumoRepository).salvar(peca);
        }

        @Test
        @DisplayName("deve lancar PecaInsumoNaoEncontradoException quando peca nao existe")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.remover(pecaId))
                    .isInstanceOf(PecaInsumoNaoEncontradoException.class);
        }
    }

    // ─────────────────────────────────────────────
    // registrarEntrada
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("registrarEntrada")
    class RegistrarEntrada {

        @Test
        @DisplayName("deve incrementar estoque e registrar movimentacao ENTRADA")
        void deveIncrementarERegistrar() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

            var captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);

            service.registrarEntrada(pecaId, 5, "Compra NF-001");

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(15);
            verify(movimentacaoRepository).salvar(captor.capture());
            assertThat(captor.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.ENTRADA);
            assertThat(captor.getValue().getQuantidade()).isEqualTo(5);
            assertThat(captor.getValue().getObservacao()).isEqualTo("Compra NF-001");
        }

        @Test
        @DisplayName("deve lancar PecaInsumoNaoEncontradoException quando peca nao existe")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.registrarEntrada(pecaId, 5, "obs"))
                    .isInstanceOf(PecaInsumoNaoEncontradoException.class);
            verifyNoInteractions(movimentacaoRepository);
        }
    }

    // ─────────────────────────────────────────────
    // registrarSaida
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("registrarSaida")
    class RegistrarSaida {

        @Test
        @DisplayName("deve decrementar estoque e registrar movimentacao SAIDA")
        void deveDecrementarERegistrar() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

            var captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);

            service.registrarSaida(pecaId, 4, "Uso interno");

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(6);
            verify(movimentacaoRepository).salvar(captor.capture());
            assertThat(captor.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.SAIDA);
            assertThat(captor.getValue().getQuantidade()).isEqualTo(4);
        }

        @Test
        @DisplayName("deve lancar IllegalArgumentException quando estoque insuficiente")
        void deveLancarExcecaoQuandoEstoqueInsuficiente() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.of(peca));

            assertThatThrownBy(() -> service.registrarSaida(pecaId, 999, "obs"))
                    .isInstanceOf(IllegalArgumentException.class);
            verifyNoInteractions(movimentacaoRepository);
        }

        @Test
        @DisplayName("deve lancar PecaInsumoNaoEncontradoException quando peca nao existe")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.registrarSaida(pecaId, 1, "obs"))
                    .isInstanceOf(PecaInsumoNaoEncontradoException.class);
        }
    }

    // ─────────────────────────────────────────────
    // registrarVenda
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("registrarVenda")
    class RegistrarVenda {

        @Test
        @DisplayName("deve decrementar estoque e registrar movimentacao VENDA")
        void deveDecrementarERegistrar() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

            var captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);

            service.registrarVenda(pecaId, 2, "Venda OS-010");

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(8);
            verify(movimentacaoRepository).salvar(captor.capture());
            assertThat(captor.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.VENDA);
            assertThat(captor.getValue().getQuantidade()).isEqualTo(2);
        }

        @Test
        @DisplayName("deve lancar PecaInsumoNaoEncontradoException quando peca nao existe")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.registrarVenda(pecaId, 1, "obs"))
                    .isInstanceOf(PecaInsumoNaoEncontradoException.class);
        }
    }

    // ─────────────────────────────────────────────
    // listarMovimentacoes
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("listarMovimentacoes")
    class ListarMovimentacoes {

        @Test
        @DisplayName("deve retornar DTOs de movimentacoes ordenadas por data desc")
        void deveRetornarDTOs() {
            var mov = MovimentacaoEstoque.builder()
                    .id(UUID.randomUUID())
                    .pecaInsumo(peca)
                    .tipoMovimentacao(TipoMovimentacao.ENTRADA)
                    .quantidade(5)
                    .build();
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.of(peca));
            when(movimentacaoRepository.buscarPorPecaInsumoOrdenadoPorDataDesc(peca))
                    .thenReturn(List.of(mov));

            var resultado = service.listarMovimentacoes(pecaId);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).tipoMovimentacao()).isEqualTo(TipoMovimentacao.ENTRADA);
        }

        @Test
        @DisplayName("deve lancar PecaInsumoNaoEncontradoException quando peca nao existe")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.listarMovimentacoes(pecaId))
                    .isInstanceOf(PecaInsumoNaoEncontradoException.class);
        }
    }

    // ─────────────────────────────────────────────
    // cancelarReserva
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("cancelarReserva")
    class CancelarReserva {

        @Test
        @DisplayName("deve incrementar estoque e registrar movimentacao AJUSTE")
        void deveIncrementarERegistrarAjuste() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

            var captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);

            service.cancelarReserva(pecaId, 3, "Cancelamento de OS");

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(13);
            verify(movimentacaoRepository).salvar(captor.capture());
            assertThat(captor.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.AJUSTE);
            assertThat(captor.getValue().getQuantidade()).isEqualTo(3);
        }

        @Test
        @DisplayName("deve lancar PecaInsumoNaoEncontradoException quando peca nao existe")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.cancelarReserva(pecaId, 1, "obs"))
                    .isInstanceOf(PecaInsumoNaoEncontradoException.class);
        }
    }

    // ─────────────────────────────────────────────
    // darEntrada
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("darEntrada")
    class DarEntrada {

        @Test
        @DisplayName("deve repor estoque de peca existente e registrar ENTRADA")
        void deveReporEstoqueDeExistente() {
            var request = new EntradaEstoqueRequestDTO(
                    pecaId, null, null, null, null, null, 5, null, "Compra NF", null);
            when(pecaInsumoRepository.buscarPorId(pecaId)).thenReturn(Optional.of(peca));
            when(pecaInsumoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

            service.darEntrada(request);

            assertThat(peca.getQuantidadeEstoque()).isEqualTo(15);
            var captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
            verify(movimentacaoRepository).salvar(captor.capture());
            assertThat(captor.getValue().getTipoMovimentacao()).isEqualTo(TipoMovimentacao.ENTRADA);
        }

        @Test
        @DisplayName("deve cadastrar nova peca quando id nao informado")
        void deveCadastrarNovaPeca() {
            var request = new EntradaEstoqueRequestDTO(
                    null, "Vela de Ignição", null,
                    new BigDecimal("25.00"), new BigDecimal("15.00"),
                    null, 40, 5, "Estoque inicial", TipoPecaInsumo.PECA);

            var salva = PecaInsumo.builder()
                    .id(UUID.randomUUID()).nome("Vela de Ignição")
                    .precoVenda(new BigDecimal("25.00")).precoCompra(new BigDecimal("15.00"))
                    .quantidadeEstoque(40).quantidadeMinima(5).tipo(TipoPecaInsumo.PECA).build();
            when(pecaInsumoRepository.salvar(any())).thenReturn(salva);

            var dto = service.darEntrada(request);

            assertThat(dto.nome()).isEqualTo("Vela de Ignição");
            assertThat(dto.quantidadeEstoque()).isEqualTo(40);
        }

        @Test
        @DisplayName("deve usar quantidadeMinima zero quando nao informada")
        void deveUsarQuantidadeMinimaZeroQuandoNula() {
            var request = new EntradaEstoqueRequestDTO(
                    null, "Peca Nova", null,
                    new BigDecimal("10.00"), new BigDecimal("8.00"),
                    null, 10, null, null, TipoPecaInsumo.INSUMO);

            var captor = ArgumentCaptor.forClass(PecaInsumo.class);
            when(pecaInsumoRepository.salvar(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            service.darEntrada(request);

            assertThat(captor.getValue().getQuantidadeMinima()).isZero();
        }

        @Test
        @DisplayName("deve lancar IllegalArgumentException quando nova peca sem nome")
        void deveLancarExcecaoNovaPecaSemNome() {
            var request = new EntradaEstoqueRequestDTO(
                    null, null, null,
                    new BigDecimal("10.00"), new BigDecimal("8.00"),
                    null, 10, null, null, TipoPecaInsumo.PECA);

            assertThatThrownBy(() -> service.darEntrada(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("nome");
        }

        @Test
        @DisplayName("deve lancar IllegalArgumentException quando nova peca sem preco")
        void deveLancarExcecaoNovaPecaSemPreco() {
            var request = new EntradaEstoqueRequestDTO(
                    null, "Peca X", null, null, null,
                    null, 10, null, null, TipoPecaInsumo.PECA);

            assertThatThrownBy(() -> service.darEntrada(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("reço");
        }

        @Test
        @DisplayName("deve lancar IllegalArgumentException quando nova peca sem tipo")
        void deveLancarExcecaoNovaPecaSemTipo() {
            var request = new EntradaEstoqueRequestDTO(
                    null, "Peca X", null,
                    new BigDecimal("10.00"), new BigDecimal("8.00"),
                    null, 10, null, null, null);

            assertThatThrownBy(() -> service.darEntrada(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tipo");
        }
    }
}
