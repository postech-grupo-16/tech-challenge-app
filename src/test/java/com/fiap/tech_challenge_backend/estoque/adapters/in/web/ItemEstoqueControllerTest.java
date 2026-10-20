package com.fiap.tech_challenge_backend.estoque.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.config.TestSecurityConfigWithAuth;
import com.fiap.tech_challenge_backend.estoque.application.dto.EntradaEstoqueRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoResponseDTO;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.AtualizarPecaInsumoUseCase;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.BuscarPecaInsumoUseCase;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.CadastrarPecaInsumoUseCase;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.DarEntradaEstoqueUseCase;
import com.fiap.tech_challenge_backend.estoque.application.ports.in.MovimentarEstoqueUseCase;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import com.fiap.tech_challenge_backend.shared.application.dto.RelatorioResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemEstoqueController.class)
@Import(TestSecurityConfigWithAuth.class)
@DisplayName("ItemEstoqueController")
class ItemEstoqueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CadastrarPecaInsumoUseCase cadastrarUseCase;

    @MockBean
    private AtualizarPecaInsumoUseCase atualizarUseCase;

    @MockBean
    private BuscarPecaInsumoUseCase buscarUseCase;

    @MockBean
    private DarEntradaEstoqueUseCase darEntradaUseCase;

    @MockBean
    private MovimentarEstoqueUseCase movimentarUseCase;

    private UUID itemId;
    private PecaInsumoResponseDTO responseDTO;
    private PecaInsumoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        itemId = UUID.randomUUID();

        responseDTO = new PecaInsumoResponseDTO(
                itemId, "Filtro de óleo", "Filtro para motor 1.0",
                BigDecimal.valueOf(35.00), BigDecimal.valueOf(25.00),
                "1 unidade", 20, 5, TipoPecaInsumo.PECA, false
        );

        requestDTO = new PecaInsumoRequestDTO(
                "Filtro de óleo", "Filtro para motor 1.0",
                BigDecimal.valueOf(35.00), BigDecimal.valueOf(25.00),
                "1 unidade", 20, 5, TipoPecaInsumo.PECA
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /estoque/itens
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /estoque/itens")
    class Cadastrar {

        @Test
        @WithMockUser
        @DisplayName("deve cadastrar item e retornar 201 com o DTO")
        void deveCadastrarItemERetornar201() throws Exception {
            when(cadastrarUseCase.cadastrar(any(PecaInsumoRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/estoque/itens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(itemId.toString()))
                    .andExpect(jsonPath("$.nome").value("Filtro de óleo"))
                    .andExpect(jsonPath("$.tipo").value("PECA"))
                    .andExpect(jsonPath("$.quantidadeEstoque").value(20));

            verify(cadastrarUseCase).cadastrar(any(PecaInsumoRequestDTO.class));
        }

        @Test
        @WithMockUser
        @DisplayName("deve retornar 400 com body inválido")
        void deveRetornar400ComBodyInvalido() throws Exception {
            PecaInsumoRequestDTO invalido = new PecaInsumoRequestDTO(
                    "", null, null, null, null, null, null, null
            );

            mockMvc.perform(post("/estoque/itens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalido)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 401 sem autenticação")
        void deveRetornar401SemAutenticacao() throws Exception {
            mockMvc.perform(post("/estoque/itens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /estoque/itens/{id}
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /estoque/itens/{id}")
    class Atualizar {

        @Test
        @WithMockUser
        @DisplayName("deve atualizar item e retornar 200")
        void deveAtualizarItemERetornar200() throws Exception {
            when(atualizarUseCase.atualizar(eq(itemId), any(PecaInsumoRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(put("/estoque/itens/{id}", itemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(itemId.toString()))
                    .andExpect(jsonPath("$.nome").value("Filtro de óleo"));

            verify(atualizarUseCase).atualizar(eq(itemId), any(PecaInsumoRequestDTO.class));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /estoque/itens/{id}
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /estoque/itens/{id}")
    class BuscarPorId {

        @Test
        @WithMockUser
        @DisplayName("deve buscar item por ID e retornar 200")
        void deveBuscarItemPorId() throws Exception {
            when(buscarUseCase.buscarPorId(itemId)).thenReturn(responseDTO);

            mockMvc.perform(get("/estoque/itens/{id}", itemId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(itemId.toString()))
                    .andExpect(jsonPath("$.nome").value("Filtro de óleo"));

            verify(buscarUseCase).buscarPorId(itemId);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /estoque/itens/abaixo-do-minimo
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /estoque/itens/abaixo-do-minimo")
    class ListarAbaixoDoMinimo {

        @Test
        @WithMockUser
        @DisplayName("deve retornar dados quando ha itens abaixo do minimo")
        void deveRetornarItensAbaixoDoMinimo() throws Exception {
            PecaInsumoResponseDTO critico = new PecaInsumoResponseDTO(
                    UUID.randomUUID(), "Pastilha de freio", null,
                    BigDecimal.valueOf(80), BigDecimal.valueOf(50),
                    null, 2, 10, TipoPecaInsumo.PECA, true
            );
            when(buscarUseCase.listarAbaixoDoMinimo()).thenReturn(List.of(critico));

            mockMvc.perform(get("/estoque/itens/abaixo-do-minimo"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem").value("Estoque carregado com sucesso"))
                    .andExpect(jsonPath("$.dados", hasSize(1)))
                    .andExpect(jsonPath("$.dados[0].nome").value("Pastilha de freio"))
                    .andExpect(jsonPath("$.totalResultados").value(1));

            verify(buscarUseCase).listarAbaixoDoMinimo();
        }

        @Test
        @WithMockUser
        @DisplayName("deve retornar vazio quando nenhum item esta abaixo do minimo")
        void deveRetornarVazioQuandoEstoqueAdequado() throws Exception {
            when(buscarUseCase.listarAbaixoDoMinimo()).thenReturn(List.of());

            mockMvc.perform(get("/estoque/itens/abaixo-do-minimo"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem")
                            .value("Nenhuma peça ou insumo encontrada com o status: ITENS ABAIXO DO MINIMO"))
                    .andExpect(jsonPath("$.totalResultados").value(0))
                    .andExpect(jsonPath("$.dados", hasSize(0)));

            verify(buscarUseCase).listarAbaixoDoMinimo();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /estoque/itens
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /estoque/itens")
    class Listar {

        @Test
        @WithMockUser
        @DisplayName("deve listar todos os itens sem filtro")
        void deveListarTodosOsItens() throws Exception {
            when(buscarUseCase.listarTodos(null)).thenReturn(List.of(responseDTO));

            mockMvc.perform(get("/estoque/itens"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].nome").value("Filtro de óleo"));

            verify(buscarUseCase).listarTodos(null);
        }

        @Test
        @WithMockUser
        @DisplayName("deve listar itens filtrados por tipo PECA")
        void deveListarItensFiltrandoPorTipo() throws Exception {
            when(buscarUseCase.listarTodos(TipoPecaInsumo.PECA)).thenReturn(List.of(responseDTO));

            mockMvc.perform(get("/estoque/itens").param("tipo", "PECA"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].tipo").value("PECA"));

            verify(buscarUseCase).listarTodos(TipoPecaInsumo.PECA);
        }

        @Test
        @WithMockUser
        @DisplayName("deve retornar lista vazia quando nao ha itens")
        void deveRetornarListaVazia() throws Exception {
            when(buscarUseCase.listarTodos(null)).thenReturn(List.of());

            mockMvc.perform(get("/estoque/itens"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /estoque/itens/entrada
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /estoque/itens/entrada")
    class DarEntrada {

        @Test
        @WithMockUser
        @DisplayName("deve dar entrada e retornar 200 com DTO")
        void deveDarEntradaERetornar200() throws Exception {
            EntradaEstoqueRequestDTO entrada = new EntradaEstoqueRequestDTO(
                    null, "Vela de ignição", "NGK",
                    BigDecimal.valueOf(15), BigDecimal.valueOf(10),
                    "4 unidades", 8, 2, "NF-001", TipoPecaInsumo.PECA
            );

            when(darEntradaUseCase.darEntrada(any(EntradaEstoqueRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/estoque/itens/entrada")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entrada)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(itemId.toString()));

            verify(darEntradaUseCase).darEntrada(any(EntradaEstoqueRequestDTO.class));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PATCH /estoque/itens/{id}/entrada
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("PATCH /estoque/itens/{id}/entrada")
    class RegistrarEntrada {

        @Test
        @WithMockUser
        @DisplayName("deve registrar entrada sem observacao e retornar relatorio")
        void deveRegistrarEntradaSemObservacao() throws Exception {
            doNothing().when(movimentarUseCase).registrarEntrada(eq(itemId), eq(10), isNull());
            when(buscarUseCase.buscarPorId(itemId)).thenReturn(responseDTO);

            mockMvc.perform(patch("/estoque/itens/{id}/entrada", itemId)
                            .param("quantidade", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem")
                            .value("Entrada de estoque registrada com sucesso | Quantidade adicionada: 10"))
                    .andExpect(jsonPath("$.totalResultados").value(1))
                    .andExpect(jsonPath("$.dados[0].nome").value("Filtro de óleo"));

            verify(movimentarUseCase).registrarEntrada(eq(itemId), eq(10), isNull());
        }

        @Test
        @WithMockUser
        @DisplayName("deve registrar entrada com observacao")
        void deveRegistrarEntradaComObservacao() throws Exception {
            doNothing().when(movimentarUseCase).registrarEntrada(eq(itemId), eq(5), eq("NF-002"));
            when(buscarUseCase.buscarPorId(itemId)).thenReturn(responseDTO);

            mockMvc.perform(patch("/estoque/itens/{id}/entrada", itemId)
                            .param("quantidade", "5")
                            .param("observacao", "NF-002"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem")
                            .value("Entrada de estoque registrada com sucesso | Quantidade adicionada: 5"));

            verify(movimentarUseCase).registrarEntrada(eq(itemId), eq(5), eq("NF-002"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PATCH /estoque/itens/{id}/saida
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("PATCH /estoque/itens/{id}/saida")
    class RegistrarSaida {

        @Test
        @WithMockUser
        @DisplayName("deve registrar saida sem observacao e retornar relatorio")
        void deveRegistrarSaidaSemObservacao() throws Exception {
            doNothing().when(movimentarUseCase).registrarSaida(eq(itemId), eq(3), isNull());
            when(buscarUseCase.buscarPorId(itemId)).thenReturn(responseDTO);

            mockMvc.perform(patch("/estoque/itens/{id}/saida", itemId)
                            .param("quantidade", "3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem")
                            .value("Saída de estoque registrada com sucesso | Quantidade removida: 3"))
                    .andExpect(jsonPath("$.totalResultados").value(1))
                    .andExpect(jsonPath("$.dados[0].nome").value("Filtro de óleo"));

            verify(movimentarUseCase).registrarSaida(eq(itemId), eq(3), isNull());
        }

        @Test
        @WithMockUser
        @DisplayName("deve registrar saida com observacao")
        void deveRegistrarSaidaComObservacao() throws Exception {
            doNothing().when(movimentarUseCase).registrarSaida(eq(itemId), eq(2), eq("Uso OS-55"));
            when(buscarUseCase.buscarPorId(itemId)).thenReturn(responseDTO);

            mockMvc.perform(patch("/estoque/itens/{id}/saida", itemId)
                            .param("quantidade", "2")
                            .param("observacao", "Uso OS-55"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem")
                            .value("Saída de estoque registrada com sucesso | Quantidade removida: 2"));

            verify(movimentarUseCase).registrarSaida(eq(itemId), eq(2), eq("Uso OS-55"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /estoque/itens/{id}
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /estoque/itens/{id}")
    class Remover {

        @Test
        @WithMockUser
        @DisplayName("deve remover item e retornar mensagem de confirmacao")
        void deveRemoverItemERetornarConfirmacao() throws Exception {
            doNothing().when(atualizarUseCase).remover(itemId);

            mockMvc.perform(delete("/estoque/itens/{id}", itemId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem").value(
                            "Item de estoque removido com sucesso. Todos os registros de utilização nas ordens de serviço foram preservados no histórico."))
                    .andExpect(jsonPath("$.totalResultados").value(0))
                    .andExpect(jsonPath("$.dados", hasSize(0)));

            verify(atualizarUseCase).remover(itemId);
        }
    }
}
