package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("IdGeneratorService - Testes Unitários")
class IdGeneratorServiceTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    private IdGeneratorService idGeneratorService;

    @BeforeEach
    void setUp() {
        when(ordemServicoRepository.buscarMaiorIdOrdemServico()).thenReturn(null);
        when(ordemServicoRepository.buscarMaiorIdOrcamento()).thenReturn(null);
        idGeneratorService = new IdGeneratorService(ordemServicoRepository);
    }

    @Test
    @DisplayName("Deve gerar ID de Ordem de Serviço com formato YYYYNNNNN")
    void testGerarIdOrdemServicoFormatoCorreto() {
        Long id = idGeneratorService.gerarIdOrdemServico();

        assertNotNull(id);
        String idStr = String.valueOf(id);

        int anoAtual = Year.now().getValue();
        String anoEsperado = String.valueOf(anoAtual);

        assertTrue(idStr.startsWith(anoEsperado), "ID deve iniciar com o ano atual");
        assertEquals(9, idStr.length(), "ID deve ter 9 dígitos (YYYYNNNNN)");
    }

    @Test
    @DisplayName("Deve gerar IDs sequenciais para Ordem de Serviço")
    void testGerarIdOrdemServicoSequencial() {
        Long id1 = idGeneratorService.gerarIdOrdemServico();
        Long id2 = idGeneratorService.gerarIdOrdemServico();
        Long id3 = idGeneratorService.gerarIdOrdemServico();

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(id3);

        assertTrue(id2 > id1, "Segundo ID deve ser maior que o primeiro");
        assertTrue(id3 > id2, "Terceiro ID deve ser maior que o segundo");

        long diff1 = id2 - id1;
        long diff2 = id3 - id2;
        assertEquals(1, diff1, "Diferença entre IDs consecutivos deve ser 1");
        assertEquals(1, diff2, "Diferença entre IDs consecutivos deve ser 1");
    }

    @Test
    @DisplayName("Deve gerar ID de Orçamento com formato YYNNNNNNN")
    void testGerarIdOrcamentoFormatoCorreto() {
        Long id = idGeneratorService.gerarIdOrcamento();

        assertNotNull(id);
        String idStr = String.valueOf(id);

        assertEquals(8, idStr.length(), "ID de Orçamento deve ter 8 dígitos (YYNNNNNNN)");

        int ultimosDoisDigitos = (Year.now().getValue() % 100);
        String expectedPrefix = String.format("%02d", ultimosDoisDigitos);
        assertTrue(idStr.startsWith(expectedPrefix), "ID deve iniciar com os 2 últimos dígitos do ano");
    }

    @Test
    @DisplayName("Deve gerar IDs sequenciais para Orçamento")
    void testGerarIdOrcamentoSequencial() {
        Long id1 = idGeneratorService.gerarIdOrcamento();
        Long id2 = idGeneratorService.gerarIdOrcamento();
        Long id3 = idGeneratorService.gerarIdOrcamento();

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(id3);

        assertTrue(id2 > id1, "Segundo ID deve ser maior que o primeiro");
        assertTrue(id3 > id2, "Terceiro ID deve ser maior que o segundo");

        long diff1 = id2 - id1;
        long diff2 = id3 - id2;
        assertEquals(1, diff1, "Diferença entre IDs consecutivos de orçamento deve ser 1");
        assertEquals(1, diff2, "Diferença entre IDs consecutivos de orçamento deve ser 1");
    }

    @Test
    @DisplayName("Deve gerar IDs para Ordem de Serviço e Orçamento independentemente")
    void testGerarIdIndependentes() {
        Long idOS1 = idGeneratorService.gerarIdOrdemServico();
        Long idOrcamento1 = idGeneratorService.gerarIdOrcamento();
        Long idOS2 = idGeneratorService.gerarIdOrdemServico();
        Long idOrcamento2 = idGeneratorService.gerarIdOrcamento();

        assertNotNull(idOS1);
        assertNotNull(idOrcamento1);
        assertNotNull(idOS2);
        assertNotNull(idOrcamento2);

        assertTrue(idOS2 > idOS1, "Segunda OS deve ter ID maior que primeira");
        assertTrue(idOrcamento2 > idOrcamento1, "Segundo orçamento deve ter ID maior que primeiro");

        String osStr = String.valueOf(idOS1);
        String orcStr = String.valueOf(idOrcamento1);

        assertEquals(9, osStr.length(), "ID de OS deve ter 9 dígitos");
        assertEquals(8, orcStr.length(), "ID de Orçamento deve ter 8 dígitos");
    }

    @Test
    @DisplayName("Deve inicializar com sequência a partir do banco de dados")
    void testInicializarSequenciaDoRepositorio() {
        when(ordemServicoRepository.buscarMaiorIdOrdemServico()).thenReturn(202600100L);
        when(ordemServicoRepository.buscarMaiorIdOrcamento()).thenReturn(26000050L);

        IdGeneratorService service = new IdGeneratorService(ordemServicoRepository);

        Long novoIdOS = service.gerarIdOrdemServico();
        Long novoIdOrcamento = service.gerarIdOrcamento();

        assertNotNull(novoIdOS);
        assertNotNull(novoIdOrcamento);

        assertTrue(novoIdOS > 202600100L, "Novo ID de OS deve ser maior que o máximo do banco");
        assertTrue(novoIdOrcamento > 26000050L, "Novo ID de Orçamento deve ser maior que o máximo do banco");
    }

    @Test
    @DisplayName("Deve tratar exceção ao inicializar sequência do banco")
    void testTratarExcecaoInicializacao() {
        when(ordemServicoRepository.buscarMaiorIdOrdemServico()).thenThrow(new RuntimeException("Erro ao consultar banco"));
        when(ordemServicoRepository.buscarMaiorIdOrcamento()).thenThrow(new RuntimeException("Erro ao consultar banco"));

        assertDoesNotThrow(() -> new IdGeneratorService(ordemServicoRepository));

        IdGeneratorService service = new IdGeneratorService(ordemServicoRepository);
        Long id = service.gerarIdOrdemServico();

        assertNotNull(id);
        String idStr = String.valueOf(id);
        int anoAtual = Year.now().getValue();
        assertTrue(idStr.startsWith(String.valueOf(anoAtual)));
    }
}
