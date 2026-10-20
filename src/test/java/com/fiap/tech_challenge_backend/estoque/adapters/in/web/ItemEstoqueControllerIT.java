package com.fiap.tech_challenge_backend.estoque.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tech_challenge_backend.estoque.application.dto.EntradaEstoqueRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import com.fiap.tech_challenge_backend.estoque.adapters.out.persistence.PecaInsumoMapper;
import com.fiap.tech_challenge_backend.estoque.adapters.out.persistence.PecaInsumoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("ItemEstoqueController - Integração")
class ItemEstoqueControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void overrideDataSourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PecaInsumoRepository pecaInsumoRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limparBase() {
        // Ordem respeitando FKs; peca_insumo usa SQL nativo para contornar
        // @Where(deleted_at IS NULL) presente no soft-delete da entidade
        jdbcTemplate.update("DELETE FROM os_pecas");
        jdbcTemplate.update("DELETE FROM movimentacoes_estoque");
        jdbcTemplate.update("DELETE FROM peca_insumo");
    }

    private PecaInsumoRequestDTO requestFiltroOleo() {
        return new PecaInsumoRequestDTO(
                "Filtro de óleo", "Filtro para motor 1.0", new BigDecimal("35.00"),
                new BigDecimal("25.00"), "1 unidade", 20, 5, TipoPecaInsumo.PECA
        );
    }

    private PecaInsumo salvarFiltroOleo() {
        return PecaInsumoMapper.toDomain(pecaInsumoRepository.save(PecaInsumoMapper.toEntity(PecaInsumo.builder()
                .nome("Filtro de óleo")
                .descricao("Filtro para motor 1.0")
                .precoVenda(new BigDecimal("35.00"))
                .precoCompra(new BigDecimal("25.00"))
                .quantidadePorUnidade("1 unidade")
                .quantidadeEstoque(20)
                .quantidadeMinima(5)
                .tipo(TipoPecaInsumo.PECA)
                .build())));
    }

    // ─────────────────────────────────────────────────────────
    // POST /estoque/itens
    // ─────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("POST /estoque/itens - deve cadastrar item e retornar 201")
    void deveCadastrarItem() throws Exception {
        mockMvc.perform(post("/estoque/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestFiltroOleo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Filtro de óleo"))
                .andExpect(jsonPath("$.tipo").value("PECA"))
                .andExpect(jsonPath("$.quantidadeEstoque").value(20))
                .andExpect(jsonPath("$.quantidadeMinima").value(5))
                .andExpect(jsonPath("$.precoVenda").value(35.00))
                .andExpect(jsonPath("$.precoCompra").value(25.00))
                .andExpect(jsonPath("$.abaixoDoMinimo").value(false));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /estoque/itens - deve retornar 400 com dados inválidos")
    void deveRetornar400AoCadastrarComDadosInvalidos() throws Exception {
        PecaInsumoRequestDTO invalido = new PecaInsumoRequestDTO(
                "", null, null, null, null, null, null, null
        );

        mockMvc.perform(post("/estoque/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /estoque/itens - deve retornar 401 sem autenticação")
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(post("/estoque/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestFiltroOleo())))
                .andExpect(status().isUnauthorized());
    }

    // ─────────────────────────────────────────────────────────
    // GET /estoque/itens/{id}
    // ─────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens/{id} - deve buscar item por ID e retornar dados corretos")
    void deveBuscarItemPorId() throws Exception {
        var peca = salvarFiltroOleo();

        mockMvc.perform(get("/estoque/itens/{id}", peca.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(peca.getId().toString()))
                .andExpect(jsonPath("$.nome").value("Filtro de óleo"))
                .andExpect(jsonPath("$.tipo").value("PECA"))
                .andExpect(jsonPath("$.quantidadeEstoque").value(20))
                .andExpect(jsonPath("$.descricao").value("Filtro para motor 1.0"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens/{id} - deve retornar 404 quando item não existe")
    void deveRetornar404AoBuscarItemInexistente() throws Exception {
        mockMvc.perform(get("/estoque/itens/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────────────────────────────────────
    // GET /estoque/itens
    // ─────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens - deve listar todos os itens cadastrados")
    void deveListarTodosOsItens() throws Exception {
        salvarFiltroOleo();
        pecaInsumoRepository.save(PecaInsumoMapper.toEntity(PecaInsumo.builder()
                .nome("Óleo lubrificante")
                .precoVenda(new BigDecimal("40.00"))
                .precoCompra(new BigDecimal("30.00"))
                .quantidadeEstoque(10)
                .quantidadeMinima(2)
                .tipo(TipoPecaInsumo.INSUMO)
                .build()));

        mockMvc.perform(get("/estoque/itens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens - deve listar itens filtrados por tipo PECA")
    void deveListarItensFiltrandoPorTipoPeca() throws Exception {
        salvarFiltroOleo();
        pecaInsumoRepository.save(PecaInsumoMapper.toEntity(PecaInsumo.builder()
                .nome("Óleo lubrificante")
                .precoVenda(new BigDecimal("40.00"))
                .precoCompra(new BigDecimal("30.00"))
                .quantidadeEstoque(10)
                .quantidadeMinima(2)
                .tipo(TipoPecaInsumo.INSUMO)
                .build()));

        mockMvc.perform(get("/estoque/itens").param("tipo", "PECA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipo").value("PECA"))
                .andExpect(jsonPath("$[0].nome").value("Filtro de óleo"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens - deve retornar lista vazia quando não há itens")
    void deveRetornarListaVaziaQuandoNaoHaItens() throws Exception {
        mockMvc.perform(get("/estoque/itens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ─────────────────────────────────────────────────────────
    // GET /estoque/itens/abaixo-do-minimo
    // ─────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens/abaixo-do-minimo - deve listar itens com estoque crítico")
    void deveListarItensAbaixoDoMinimo() throws Exception {
        pecaInsumoRepository.save(PecaInsumoMapper.toEntity(PecaInsumo.builder()
                .nome("Pastilha de freio")
                .precoVenda(new BigDecimal("80.00"))
                .precoCompra(new BigDecimal("50.00"))
                .quantidadeEstoque(2)
                .quantidadeMinima(10)
                .tipo(TipoPecaInsumo.PECA)
                .build()));

        mockMvc.perform(get("/estoque/itens/abaixo-do-minimo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Estoque carregado com sucesso"))
                .andExpect(jsonPath("$.dados", hasSize(1)))
                .andExpect(jsonPath("$.dados[0].nome").value("Pastilha de freio"))
                .andExpect(jsonPath("$.dados[0].abaixoDoMinimo").value(true));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /estoque/itens/abaixo-do-minimo - deve retornar vazio quando estoque está adequado")
    void deveRetornarVazioQuandoEstoqueAdequado() throws Exception {
        salvarFiltroOleo(); // quantidadeEstoque(20) > quantidadeMinima(5)

        mockMvc.perform(get("/estoque/itens/abaixo-do-minimo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem")
                        .value("Nenhuma peça ou insumo encontrada com o status: ITENS ABAIXO DO MINIMO"))
                .andExpect(jsonPath("$.dados", hasSize(0)))
                .andExpect(jsonPath("$.totalResultados").value(0));
    }

    // ─────────────────────────────────────────────────────────
    // PUT /estoque/itens/{id}
    // ─────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("PUT /estoque/itens/{id} - deve atualizar item com sucesso")
    void deveAtualizarItem() throws Exception {
        var peca = salvarFiltroOleo();

        PecaInsumoRequestDTO atualizacao = new PecaInsumoRequestDTO(
                "Filtro de óleo Premium", "Filtro atualizado", new BigDecimal("45.00"),
                new BigDecimal("30.00"), "1 unidade", 15, 3, TipoPecaInsumo.PECA
        );

        mockMvc.perform(put("/estoque/itens/{id}", peca.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Filtro de óleo Premium"))
                .andExpect(jsonPath("$.precoVenda").value(45.00))
                .andExpect(jsonPath("$.quantidadeEstoque").value(15))
                .andExpect(jsonPath("$.quantidadeMinima").value(3));
    }

    // ─────────────────────────────────────────────────────────
    // PATCH /estoque/itens/{id}/entrada
    // ─────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("PATCH /estoque/itens/{id}/entrada - deve registrar entrada e incrementar estoque")
    void deveRegistrarEntradaDeEstoque() throws Exception {
        var peca = salvarFiltroOleo(); // estoque inicial: 20

        mockMvc.perform(patch("/estoque/itens/{id}/entrada", peca.getId())
                        .param("quantidade", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem")
                        .value("Entrada de estoque registrada com sucesso | Quantidade adicionada: 10"))
                .andExpect(jsonPath("$.totalResultados").value(1))
                .andExpect(jsonPath("$.dados[0].quantidadeEstoque").value(30));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /estoque/itens/{id}/entrada - deve registrar entrada com observação")
    void deveRegistrarEntradaComObservacao() throws Exception {
        var peca = salvarFiltroOleo(); // estoque inicial: 20

        mockMvc.perform(patch("/estoque/itens/{id}/entrada", peca.getId())
                        .param("quantidade", "5")
                        .param("observacao", "Compra NF-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados[0].quantidadeEstoque").value(25));
    }

    // ─────────────────────────────────────────────────────────
    // PATCH /estoque/itens/{id}/saida
    // ─────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("PATCH /estoque/itens/{id}/saida - deve registrar saída e decrementar estoque")
    void deveRegistrarSaidaDeEstoque() throws Exception {
        var peca = salvarFiltroOleo(); // estoque inicial: 20

        mockMvc.perform(patch("/estoque/itens/{id}/saida", peca.getId())
                        .param("quantidade", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem")
                        .value("Saída de estoque registrada com sucesso | Quantidade removida: 5"))
                .andExpect(jsonPath("$.totalResultados").value(1))
                .andExpect(jsonPath("$.dados[0].quantidadeEstoque").value(15));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /estoque/itens/{id}/saida - deve retornar 400 quando quantidade excede estoque")
    void deveRetornar400AoRegistrarSaidaComQuantidadeInsuficiente() throws Exception {
        var peca = salvarFiltroOleo(); // estoque: 20

        mockMvc.perform(patch("/estoque/itens/{id}/saida", peca.getId())
                        .param("quantidade", "99"))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────────────────
    // POST /estoque/itens/entrada
    // ─────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("POST /estoque/itens/entrada - deve criar novo item via entrada unificada")
    void deveDarEntradaCriandoNovoItem() throws Exception {
        EntradaEstoqueRequestDTO request = new EntradaEstoqueRequestDTO(
                null, "Vela de ignição", "Vela NGK", new BigDecimal("15.00"),
                new BigDecimal("10.00"), "4 unidades", 8, 2, "NF-002", TipoPecaInsumo.PECA
        );

        mockMvc.perform(post("/estoque/itens/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Vela de ignição"))
                .andExpect(jsonPath("$.tipo").value("PECA"))
                .andExpect(jsonPath("$.quantidadeEstoque").value(8));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /estoque/itens/entrada - deve repor estoque de item existente via ID")
    void deveDarEntradaRepondoEstoqueExistente() throws Exception {
        var peca = salvarFiltroOleo(); // estoque inicial: 20

        EntradaEstoqueRequestDTO request = new EntradaEstoqueRequestDTO(
                peca.getId(), null, null, null, null, null, 15, null, "Reposição mensal", null
        );

        mockMvc.perform(post("/estoque/itens/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Filtro de óleo"))
                .andExpect(jsonPath("$.quantidadeEstoque").value(35));
    }

    // ─────────────────────────────────────────────────────────
    // DELETE /estoque/itens/{id}
    // ─────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("DELETE /estoque/itens/{id} - deve remover item e retornar mensagem de confirmação")
    void deveDeletarItem() throws Exception {
        var peca = salvarFiltroOleo();

        mockMvc.perform(delete("/estoque/itens/{id}", peca.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value(
                        "Item de estoque removido com sucesso. Todos os registros de utilização nas ordens de serviço foram preservados no histórico."))
                .andExpect(jsonPath("$.totalResultados").value(0))
                .andExpect(jsonPath("$.dados", hasSize(0)));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /estoque/itens/{id} - deve retornar 404 quando item não existe")
    void deveRetornar404AoDeletarItemInexistente() throws Exception {
        mockMvc.perform(delete("/estoque/itens/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /estoque/itens/{id} - item soft-deletado não deve ser encontrado após remoção")
    void itemDeletadoNaoDeveSerEncontradoAposRemocao() throws Exception {
        var peca = salvarFiltroOleo();

        mockMvc.perform(delete("/estoque/itens/{id}", peca.getId()))
                .andExpect(status().isOk());

        // Soft-delete: @Where(deleted_at IS NULL) filtra o item → 404
        mockMvc.perform(get("/estoque/itens/{id}", peca.getId()))
                .andExpect(status().isNotFound());
    }
}
