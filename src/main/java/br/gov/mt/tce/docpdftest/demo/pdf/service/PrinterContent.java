package br.gov.mt.tce.docpdftest.demo.pdf.service;

import br.gov.mt.tce.docpdftest.demo.pdf.customrenderer.ColumnRendererAlternateArea;
import br.gov.mt.tce.pdf.entity.Materia;
import br.gov.mt.tce.pdf.enumerados.LarguraPaginaEnum;
import br.gov.mt.tce.pdf.htmlcustom.TagAppender;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.RootRenderer;

import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Objetivo: copiar o conteúdo do renderer temporário para para o final.
 * Esta classe não vai guardar estado.
 */

public class PrinterContent {

    public static void addHtmlToDocument(Document document, final Materia materia) throws IOException {
        String htmlMateria = new TagAppender().loadHtmlMateriaContent(materia);

        List<IElement> iElements = HtmlConverter.convertToElements(htmlMateria, new ConverterProperties());

        for (IElement element : iElements)
            document.add((IBlockElement) element);

        System.out.println(materia.getTitulo());
    }

    /**
     * Se a ordem de impressão da matéria atual mudar;
     */
    public static void moveContentWrittenChangeDirection(ColumnRendererAlternateArea finalRenderer,
                                                         ColumnRendererAlternateArea temporaryRenderer,
                                                         Document document,
                                                         LarguraPaginaEnum writtenDirection) {
        float columnHeight = 0F;
        finalRenderer.setWrittenDirection(writtenDirection);
//        RootRenderer rootRenderer = temporaryRenderer.getDocument().getRenderer();
//        printOccupiedAreasOfTableRenderers(document.getRenderer());
        //Calcula o tamanho de cada coluna ou o tamanho do conteúdo inteiro em caso de página inteira.
        //columnHeight = writtenDirection.isColuna() ? temporaryRenderer.calculateColumnHeightToSplitContent() : temporaryRenderer.getOccupedPageArea();

        float sumChildHeight = temporaryRenderer.sumChildsHeight();
        columnHeight = writtenDirection.isColuna() ? sumChildHeight / 2 : temporaryRenderer.getOccupedPageArea();

        finalRenderer.setContentHeight(columnHeight);
        finalRenderer.setTotalContentHeight(sumChildHeight);
        for (IRenderer child : temporaryRenderer.getChildRenderers()) {
            if (child.toString().contains("EXTRATO DO TERMO DE COOP"))
                System.out.println(child);

            finalRenderer.addChild(child);
        }

    }

    /**
     * Se a área de impressão da página temporária acabar
     */
    public static void moveContentEndPage(ColumnRendererAlternateArea finalRenderer,
                                          ColumnRendererAlternateArea temporaryRenderer) {
        System.out.println("Falta implementar...");
    }

    /**
     * Se existe matéria no renderer temporário e todas as matérias já foram processadas;
     */
    public static void moveContentAtEnd(ColumnRendererAlternateArea finalRenderer,
                                        ColumnRendererAlternateArea temporaryRenderer,
                                        LarguraPaginaEnum writtenDirection) {

        float columnHeight = 0F;

//        finalRenderer.setLastContent(finalRenderer.sumChildsHeight() > 0);
        finalRenderer.setLastContent(true);
        finalRenderer.setWrittenDirection(writtenDirection);

        //columnHeight = writtenDirection.isColuna() ? temporaryRenderer.calculateColumnHeightToSplitContent() : temporaryRenderer.getOccupedPageArea();

        PdfDocument pdfDocument = temporaryRenderer.getDocument().getPdfDocument();
        pdfDocument.getNumberOfPages();

        /*PdfPage lastPage = pdfDocument.getLastPage();
        int lastPageNumber = pdfDocument.getPageNumber(lastPage);*/

        float sumChildHeight = temporaryRenderer.sumChildsHeight();
        //columnHeight = writtenDirection.isColuna() ? sumChildHeight / 2 : temporaryRenderer.getOccupedPageArea();

        finalRenderer.setTotalContentHeight(sumChildHeight);
        //finalRenderer.setContentHeight(columnHeight);
        for (IRenderer child : temporaryRenderer.getChildRenderers()) {
            if (child.toString().contains("EXTRATO DO TERMO DE COOP"))
                System.out.println(child);

            finalRenderer.addChild(child);
        }
    }


    private static void printOccupiedAreasOfTableRenderers(IRenderer currentNode) {
        System.out.println("Table renderer with occupied area: " + currentNode.getOccupiedArea());
        if (currentNode.getClass().equals(Div.class)) {
            System.out.println("Table renderer with occupied area: " + currentNode.getOccupiedArea());
        }
        for (IRenderer child : currentNode.getChildRenderers()) {
            printOccupiedAreasOfTableRenderers(child);
        }
    }
}
