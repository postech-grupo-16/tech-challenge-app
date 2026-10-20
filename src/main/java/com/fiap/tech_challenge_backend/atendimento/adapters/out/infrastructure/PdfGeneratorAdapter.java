package com.fiap.tech_challenge_backend.atendimento.adapters.out.infrastructure;

import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PdfGeneratorPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PecaInsumoCatalogoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Component
public class PdfGeneratorAdapter implements PdfGeneratorPort {

    private static final String OFICINA_NOME = "Oficina Grupo 15";
    private static final String OFICINA_ENDERECO = "Rua das Oficinas, 123 - São Paulo, SP";
    private static final String OFICINA_TELEFONE = "(11) 3000-0000";
    private static final String OFICINA_EMAIL = "oficinagrupo015@gmail.com";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Cores padrão do email
    private static final Color COR_HEADER = new DeviceRgb(44, 62, 80);       // #2c3e50 - Azul escuro
    private static final Color COR_CONTENT = new DeviceRgb(236, 240, 241);   // #ecf0f1 - Cinza claro
    private static final Color COR_BORDER = new DeviceRgb(52, 152, 219);     // #3498db - Azul claro
    private static final Color COR_DESTAQUE = new DeviceRgb(39, 174, 96);    // #27ae60 - Verde
    private static final Color COR_BRANCO = new DeviceRgb(255, 255, 255);    // Branco

    private final PecaInsumoCatalogoRepositoryPort pecaInsumoRepository;

    public PdfGeneratorAdapter(PecaInsumoCatalogoRepositoryPort pecaInsumoRepository) {
        this.pecaInsumoRepository = pecaInsumoRepository;
    }

    @Override
    public byte[] gerarDocumentoTexto(OsOrcamento orcamento, Veiculo veiculo) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc, PageSize.A4);
            doc.setMargins(15, 15, 15, 15);

            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont fontSmall = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

            adicionarCabecalho(doc, fontBold, fontRegular);

            if (orcamento.getTipo() == TipoOrcamento.ADICIONAL) {
                adicionarAlertaOrcamentoAdicional(doc, orcamento, fontBold, fontRegular);
                doc.add(new Paragraph("\n").setFontSize(6));
            }

            adicionarDadosOrcamento(doc, orcamento, fontBold, fontRegular);
            doc.add(new Paragraph("\n").setFontSize(6));
            adicionarDadosVeiculo(doc, veiculo, fontBold, fontRegular);
            doc.add(new Paragraph("\n").setFontSize(6));
            adicionarTabelaServicos(doc, orcamento, fontBold, fontRegular);
            doc.add(new Paragraph("\n").setFontSize(6));
            adicionarTabelaPecas(doc, orcamento, fontBold, fontRegular);
            doc.add(new Paragraph("\n").setFontSize(8));
            adicionarResumoFinanceiro(doc, orcamento, fontBold, fontRegular);
            doc.add(new Paragraph("\n").setFontSize(6));
            adicionarRodape(doc, fontSmall, fontBold);

            doc.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar PDF de orçamento", e);
        }
    }

    private void adicionarCabecalho(Document doc, PdfFont fontBold, PdfFont fontRegular) {
        // Tabela para cabeçalho com fundo colorido
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(0)
                .setMarginTop(0);

        Cell headerCell = new Cell()
                .add(new Paragraph(OFICINA_NOME)
                        .setFont(fontBold)
                        .setFontSize(20)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(COR_BRANCO)
                        .setMarginTop(8)
                        .setMarginBottom(4))
                .add(new Paragraph(OFICINA_ENDERECO)
                        .setFont(fontRegular)
                        .setFontSize(9)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(COR_BRANCO)
                        .setMarginBottom(2))
                .add(new Paragraph(String.format("%s | %s", OFICINA_TELEFONE, OFICINA_EMAIL))
                        .setFont(fontRegular)
                        .setFontSize(8)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(COR_BRANCO)
                        .setMarginBottom(8))
                .setBackgroundColor(COR_HEADER)
                .setBorder(new SolidBorder(2f))
                .setPadding(0);

        headerTable.addCell(headerCell);
        doc.add(headerTable);

        // Título do documento
        Paragraph titulo = new Paragraph("ORÇAMENTO DE MANUTENÇÃO E REPARO VEICULAR")
                .setFont(fontBold)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(COR_HEADER)
                .setMarginTop(12)
                .setMarginBottom(8);
        doc.add(titulo);
    }

    private void adicionarAlertaOrcamentoAdicional(Document doc, OsOrcamento orcamento, PdfFont fontBold, PdfFont fontRegular) {
        Table alertTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setWidth(UnitValue.createPercentValue(100));

        Cell alertCell = new Cell()
                .add(new Paragraph("⚠️ ORÇAMENTO ADICIONAL - PROBLEMAS IDENTIFICADOS")
                        .setFont(fontBold)
                        .setFontSize(12)
                        .setFontColor(COR_BRANCO)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(10))
                .add(new Paragraph("Durante a execução dos serviços diagnósticos, nossos especialistas identificaram problemas adicionais que requerem atenção técnica. Este orçamento contempla os serviços necessários para resolver estas questões e garantir a segurança do veículo.")
                        .setFont(fontRegular)
                        .setFontSize(9)
                        .setFontColor(COR_BRANCO)
                        .setTextAlignment(TextAlignment.JUSTIFIED)
                        .setMarginTop(0)
                        .setMarginBottom(10))
                .add(new Paragraph("Para dúvidas ou esclarecimentos sobre os problemas encontrados, favor entrar em contato com o mecânico responsável.")
                        .setFont(fontRegular)
                        .setFontSize(8)
                        .setFontColor(new DeviceRgb(255, 200, 0))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(0))
                .setBackgroundColor(new DeviceRgb(220, 53, 69))
                .setBorder(new SolidBorder(1.5f))
                .setPadding(12);

        alertTable.addCell(alertCell);
        doc.add(alertTable);
    }

    private void adicionarDadosOrcamento(Document doc, OsOrcamento orcamento, PdfFont fontBold, PdfFont fontRegular) {
        Paragraph secao = new Paragraph("INFORMAÇÕES DO ORÇAMENTO")
                .setFont(fontBold)
                .setFontSize(11)
                .setFontColor(COR_HEADER)
                .setMarginBottom(6);
        doc.add(secao);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        table.addCell(criarCelulaInfo("Número", orcamento.getId().toString(), fontBold, fontRegular))
                .addCell(criarCelulaInfo("Data de Criação",
                        orcamento.getDataCriacao() != null
                                ? orcamento.getDataCriacao().format(DATE_FORMATTER)
                                : "N/A",
                        fontBold, fontRegular))
                .addCell(criarCelulaInfo("Prazo Estimado",
                        orcamento.getPrazoEstipulado() != null
                                ? orcamento.getPrazoEstipulado().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                : "N/A",
                        fontBold, fontRegular))
                .addCell(criarCelulaInfo("Status", orcamento.getStatus().toString(), fontBold, fontRegular));

        doc.add(table);
    }

    private void adicionarDadosVeiculo(Document doc, Veiculo veiculo, PdfFont fontBold, PdfFont fontRegular) {
        Paragraph secao = new Paragraph("INFORMAÇÕES DO VEÍCULO")
                .setFont(fontBold)
                .setFontSize(11)
                .setFontColor(COR_HEADER)
                .setMarginBottom(6);
        doc.add(secao);

        if (veiculo != null) {
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                    .setWidth(UnitValue.createPercentValue(100));

            table.addCell(criarCelulaInfo("Modelo", veiculo.getModelo(), fontBold, fontRegular))
                    .addCell(criarCelulaInfo("Placa", veiculo.getPlaca().toString(), fontBold, fontRegular))
                    .addCell(criarCelulaInfo("Ano", veiculo.getAno().toString(), fontBold, fontRegular));

            doc.add(table);
        }
    }

    private void adicionarTabelaServicos(Document doc, OsOrcamento orcamento, PdfFont fontBold, PdfFont fontRegular) {
        Paragraph secao = new Paragraph("SERVIÇOS E MÃO DE OBRA")
                .setFont(fontBold)
                .setFontSize(11)
                .setFontColor(COR_HEADER)
                .setMarginBottom(6);
        doc.add(secao);

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(criarCelulaHeader("Descrição", fontBold))
                .addHeaderCell(criarCelulaHeader("Valor Unitário", fontBold))
                .addHeaderCell(criarCelulaHeader("Subtotal", fontBold));

        if (orcamento.getServicos() != null && !orcamento.getServicos().isEmpty()) {
            boolean alternado = false;
            for (var servico : orcamento.getServicos()) {
                table.addCell(criarCelulaCorpo(servico.getServico().getNome(), fontRegular, alternado))
                        .addCell(criarCelulaCorpoAlinhada(formatarCurrency(servico.getPrecoMaoDeObraAplicado()), fontRegular, alternado))
                        .addCell(criarCelulaCorpoAlinhada(formatarCurrency(servico.getPrecoMaoDeObraAplicado()), fontRegular, alternado));
                alternado = !alternado;
            }
        } else {
            table.addCell(new Cell(1, 3)
                    .add(new Paragraph("Nenhum serviço adicionado").setFont(fontRegular).setFontSize(9))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(8));
        }

        doc.add(table);
    }

    private void adicionarTabelaPecas(Document doc, OsOrcamento orcamento, PdfFont fontBold, PdfFont fontRegular) {
        Paragraph secao = new Paragraph("PEÇAS E INSUMOS")
                .setFont(fontBold)
                .setFontSize(11)
                .setFontColor(COR_HEADER)
                .setMarginBottom(6);
        doc.add(secao);

        Table table = new Table(UnitValue.createPercentArray(new float[]{2.5f, 0.8f, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(criarCelulaHeader("Descrição", fontBold))
                .addHeaderCell(criarCelulaHeader("Qtd.", fontBold))
                .addHeaderCell(criarCelulaHeader("Valor Unit.", fontBold))
                .addHeaderCell(criarCelulaHeader("Subtotal", fontBold));

        if (orcamento.getPecas() != null && !orcamento.getPecas().isEmpty()) {
            boolean alternado = false;
            for (var peca : orcamento.getPecas()) {
                BigDecimal subtotal = peca.getPrecoVendaAplicado().multiply(BigDecimal.valueOf(peca.getQuantidade()));
                String descricao = pecaInsumoRepository.buscarPorId(peca.getPecaInsumoId())
                        .map(p -> p.getDescricao())
                        .orElse("—");
                table.addCell(criarCelulaCorpo(descricao, fontRegular, alternado))
                        .addCell(criarCelulaCorpoAlinhada(peca.getQuantidade().toString(), fontRegular, alternado))
                        .addCell(criarCelulaCorpoAlinhada(formatarCurrency(peca.getPrecoVendaAplicado()), fontRegular, alternado))
                        .addCell(criarCelulaCorpoAlinhada(formatarCurrency(subtotal), fontRegular, alternado));
                alternado = !alternado;
            }
        } else {
            table.addCell(new Cell(1, 4)
                    .add(new Paragraph("Nenhuma peça adicionada").setFont(fontRegular).setFontSize(9))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(8));
        }

        doc.add(table);
    }

    private void adicionarResumoFinanceiro(Document doc, OsOrcamento orcamento, PdfFont fontBold, PdfFont fontRegular) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setWidth(UnitValue.createPercentValue(100));

        Cell resumoCell = new Cell()
                .add(new Paragraph("VALOR TOTAL DO ORÇAMENTO")
                        .setFont(fontBold)
                        .setFontSize(10)
                        .setFontColor(COR_HEADER)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(8))
                .add(new Paragraph(formatarCurrency(orcamento.getValorTotal()))
                        .setFont(fontBold)
                        .setFontSize(18)
                        .setFontColor(COR_DESTAQUE)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(0))
                .setBackgroundColor(COR_CONTENT)
                .setBorder(new SolidBorder(2f))
                .setPadding(16)
                .setTextAlignment(TextAlignment.CENTER);

        table.addCell(resumoCell);
        doc.add(table);
    }

    private void adicionarRodape(Document doc, PdfFont fontSmall, PdfFont fontBold) {
        // Rodapé com informações
        Table rodapeTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(0)
                .setMarginBottom(0);

        Cell rodapeCell = new Cell()
                .add(new Paragraph("Validade do Orçamento: 30 dias")
                        .setFont(fontSmall)
                        .setFontSize(8)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(4))
                .add(new Paragraph("Para mais informações, entre em contato através dos telefones ou email acima.")
                        .setFont(fontSmall)
                        .setFontSize(7)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(0))
                .setBackgroundColor(COR_CONTENT)
                .setBorder(new SolidBorder(1f))
                .setPadding(8)
                .setFontColor(COR_HEADER);

        rodapeTable.addCell(rodapeCell);
        doc.add(rodapeTable);

        // Mensagem legal final
        Paragraph legal = new Paragraph("Documento gerado eletronicamente. Não requer assinatura.")
                .setFont(fontSmall)
                .setFontSize(7)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(150, 150, 150))
                .setMarginTop(8);
        doc.add(legal);
    }

    private Cell criarCelulaInfo(String label, String valor, PdfFont fontBold, PdfFont fontRegular) {
        return new Cell()
                .add(new Paragraph(label)
                        .setFont(fontBold)
                        .setFontSize(8)
                        .setFontColor(COR_HEADER)
                        .setMarginBottom(2))
                .add(new Paragraph(valor)
                        .setFont(fontRegular)
                        .setFontSize(9)
                        .setMarginTop(0))
                .setBackgroundColor(COR_CONTENT)
                .setBorder(new SolidBorder(0.5f))
                .setPadding(6)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell criarCelulaHeader(String conteudo, PdfFont font) {
        return new Cell()
                .add(new Paragraph(conteudo)
                        .setFont(font)
                        .setFontSize(9)
                        .setFontColor(COR_BRANCO))
                .setBackgroundColor(COR_HEADER)
                .setBorder(new SolidBorder(0.5f))
                .setPadding(7)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell criarCelulaCorpo(String conteudo, PdfFont font, boolean alternado) {
        Color bgColor = alternado ? COR_CONTENT : COR_BRANCO;
        return new Cell()
                .add(new Paragraph(conteudo)
                        .setFont(font)
                        .setFontSize(9))
                .setBackgroundColor(bgColor)
                .setBorder(new SolidBorder(0.3f))
                .setPadding(6);
    }

    private Cell criarCelulaCorpoAlinhada(String conteudo, PdfFont font, boolean alternado) {
        Color bgColor = alternado ? COR_CONTENT : COR_BRANCO;
        return new Cell()
                .add(new Paragraph(conteudo)
                        .setFont(font)
                        .setFontSize(9))
                .setBackgroundColor(bgColor)
                .setBorder(new SolidBorder(0.3f))
                .setPadding(6)
                .setTextAlignment(TextAlignment.RIGHT);
    }

    private String formatarCurrency(BigDecimal valor) {
        return String.format("R$ %.2f", valor);
    }
}
