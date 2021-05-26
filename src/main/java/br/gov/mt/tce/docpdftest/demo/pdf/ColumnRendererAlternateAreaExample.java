package br.gov.mt.tce.docpdftest.demo.pdf;

import br.gov.mt.tce.docpdftest.demo.pdf.config.LayoutConfig;
import br.gov.mt.tce.docpdftest.demo.pdf.customrenderer.ColumnRendererAlternateArea;
import br.gov.mt.tce.docpdftest.demo.pdf.event.MoveContentEndPageEventHandler;
import br.gov.mt.tce.docpdftest.demo.pdf.service.PrinterContent;
import br.gov.mt.tce.pdf.entity.Materia;
import br.gov.mt.tce.pdf.enumerados.LarguraPaginaEnum;
import br.gov.mt.tce.pdf.htmlcustom.TagAppender;
import br.gov.mt.tce.pdf.service.MateriaService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ColumnRendererAlternateAreaExample {

    public static final String SRC = "src/main/resources/text/materia_01.txt";
    public static final String DEST = "src/main/resources/result/ColumnRendererAlternateAreaExample.pdf";
    public static final String PAGE_TEMP = "src/main/resources/result/pageTemp.pdf";

    public static void main(String args[]) throws Exception {
        new ColumnRendererAlternateAreaExample().executeWithMaterias();
    }

    private Document createNewDocument(final String sourcePath) throws FileNotFoundException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(sourcePath));
        Document document = new Document(pdfDocument, PageSize.A4);
        return document;
    }

    public void executeWithMaterias() throws IOException {
        List<Materia> allMaterias = new MateriaService().buildMateriaList(50  , 2);

        /* Final renderer */
        //Document finalDocument = createNewDocument(DEST);
        PdfDocument finalPdfDocument = new PdfDocument(new PdfWriter(DEST));
        Document finalDocument = new Document(finalPdfDocument, PageSize.A4);
        ColumnRendererAlternateArea finalRenderer = new ColumnRendererAlternateArea(finalDocument, LayoutConfig.PAGE_COLUMN, LayoutConfig.COLUMNS);
        finalDocument.setRenderer(finalRenderer);
        //finalPdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new MoveContentEndPageEventHandler(finalRenderer, finalRenderer, new ArrayList<Materia>()));

        /* Temporary renderer */
        //Document pageTemp = createNewDocument(PAGE_TEMP);
        //TODO - Verificar se o doc. temporário precisa usar o renderizador customizado. Eu acho que não pq pego os apenas os elementos dele sem considerar formatação
        PdfDocument tempPdfDocument = new PdfDocument(new PdfWriter(PAGE_TEMP));
        Document tempDocument = new Document(tempPdfDocument, PageSize.A4);
        ColumnRendererAlternateArea tempRenderer = new ColumnRendererAlternateArea(tempDocument, LayoutConfig.PAGE_COLUMN, LayoutConfig.COLUMNS);
        tempDocument.setRenderer(tempRenderer);
        tempRenderer.setTemporaryRenderer(true);

        MoveContentEndPageEventHandler moveContentEndPageEventHandler = new MoveContentEndPageEventHandler(finalRenderer);
        finalPdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, moveContentEndPageEventHandler);
        moveContentEndPageEventHandler.setTemporaryRenderer(tempRenderer);

        tempRenderer.setWrittenDirection(LarguraPaginaEnum.COLUNA);
        finalRenderer.setWrittenDirection(LarguraPaginaEnum.COLUNA);

        LarguraPaginaEnum lastLayoutRendered = null;
        LarguraPaginaEnum currentLayout;
        boolean isFirstIteration = true;
        boolean rerestartPageTemp = false;


        List<Materia> materiasPage = new ArrayList<>();

        for (int posicaoAtual = 0; posicaoAtual < allMaterias.size(); posicaoAtual++) {

            Materia materia = allMaterias.get(posicaoAtual);
            currentLayout = materia.getLarguraPagina();

            if (isFirstIteration) {
                isFirstIteration = false;
                lastLayoutRendered = currentLayout;
            }


            if (materia.getTitulo().contains("12 - EXTRATO DO TERMO DE COOP")) {
                System.out.println("Cheguei na matéria " + materia.getTitulo());
                System.out.println("..");
            }
            if (currentLayoutHasChanged(currentLayout, lastLayoutRendered)) {

                System.out.println("\n\n Layout impressão alterado. ***");
                System.out.println("Start moving content to finalRenderer. --------------------------------------------------------------");
                PrinterContent.moveContentWrittenChangeDirection(finalRenderer, tempRenderer, tempDocument, lastLayoutRendered);
                System.out.println("finish moving content to finalRenderer. -------------------------------------------------------------- \n\n");

                rerestartPageTemp = true;
                lastLayoutRendered = currentLayout;
            }

            if (rerestartPageTemp) {
                System.out.println("Restarting tempRenderer... ***");

                tempDocument = createNewDocument(PAGE_TEMP);
                tempRenderer = new ColumnRendererAlternateArea(tempDocument, LayoutConfig.PAGE_COLUMN, LayoutConfig.COLUMNS);
                moveContentEndPageEventHandler.setTemporaryRenderer(tempRenderer);
                tempRenderer.setWrittenDirection(currentLayout);
                tempRenderer.setTemporaryRenderer(true);
                tempDocument.setRenderer(tempRenderer);
                rerestartPageTemp = false;
            }

            System.out.println("tempRenderer printing.");
            //addHtmlToDocument(tempDocument, materia);
            PrinterContent.addHtmlToDocument(tempDocument, materia);
            materiasPage.add(materia);
        }

        if (!materiasPage.isEmpty()) {
            PrinterContent.moveContentAtEnd(finalRenderer, tempRenderer, LarguraPaginaEnum.COLUNA);
        }

        finalRenderer.flush();
        finalDocument.close();
    }

    public void execute() throws Exception {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);

        String text_data = "";
        File legacy_file = new File(SRC);
        PdfDocument pdf = new PdfDocument(new PdfWriter(DEST));
        Document document = new Document(pdf).setTextAlignment(TextAlignment.JUSTIFIED);
        document.setMargins(LayoutConfig.MARGIN_TOP, LayoutConfig.MARGIN_RIGHT, LayoutConfig.MARGIN_BOTTOM, LayoutConfig.MARGIN_LEFT);
        document.setBackgroundColor(ColorConstants.BLACK);

        pdf.addNewPage(LayoutConfig.PAGE_SIZE);

        ColumnRendererAlternateArea renderer = new ColumnRendererAlternateArea(document, LayoutConfig.PAGE_COLUMN, LayoutConfig.COLUMNS);
        document.setRenderer(renderer);
        AreaBreak bodyStart = new AreaBreak(AreaBreakType.NEXT_AREA);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(legacy_file.getAbsolutePath()), "euc-kr"));
            String current_line = "";
            int line_idx = 0;
            while (null != (current_line = reader.readLine())) {
                if (line_idx == 0) {
                    document.add(new Paragraph(current_line).setFont(font));
                } else if (line_idx == 1) {
                    document.add(new Paragraph(current_line).setFont(bold).setFontSize(26));
                } else if (line_idx == 2) {
                    document.add(new Paragraph(current_line).setFont(font));
                } else {
                    document.add(new Paragraph(current_line).setFont(font));
                }
                if (line_idx == 26) {
                    renderer.setWrittenDirection(LarguraPaginaEnum.COLUNA);
                    document.add(bodyStart);
                }
                line_idx++;
            }
            reader.close();
        } catch (IOException e) {
            //logger.debug(e.getMessage());
        }
        renderer.flush();
        document.close();
    }

    private void addHtmlToDocument(Document document, Materia materia) throws IOException {
        String htmlMateria = new TagAppender().loadHtmlMateriaContent(materia);

        List<IElement> iElements = HtmlConverter.convertToElements(htmlMateria, new ConverterProperties());

        for (IElement element : iElements)
            document.add((IBlockElement) element);

        System.out.println(materia.getTitulo());
    }

    private boolean currentLayoutHasChanged(final LarguraPaginaEnum currentLayout, final LarguraPaginaEnum lastLayoutRenderer) {
        return lastLayoutRenderer != null &&
                !lastLayoutRenderer.equals(currentLayout);
    }
}
