package br.gov.mt.tce.pdf;

import br.gov.mt.tce.pdf.customcomponent.LayoutColumn;
import br.gov.mt.tce.pdf.entity.Materia;
import br.gov.mt.tce.pdf.enumerados.LarguraPaginaEnum;
import br.gov.mt.tce.pdf.htmlcustom.TagAppender;
import br.gov.mt.tce.pdf.mergepdf.PdfDenseMerger;
import br.gov.mt.tce.pdf.service.MateriaService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.property.AreaBreakType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class PDFBuilder {

    public static final String ARIAL_FONT = "src/main/resources/pdf/fonts/arial.ttf";
    public static final String TIMES_NEW_ROMAN = "src/main/resources/fonts/times-new-roman-ce-3.ttf";
    public static final String DEST = "src/main/resources/result/PDFBuilder.pdf";
    public static final String RESULT_FOLDER = "src/main/resources/result/ColumnRendererAlternateAreaExample.pdf";

    private static final String BASE_URI = "/Users/Douglas/Documents/Desenv/itext7-examples/src/main/resources";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        //new PDFBuilder().createPdf(DEST);
        new PDFBuilder().testMergeGrandizerFilesA5();
    }

    public void testMergeGrandizerFilesA5() throws IOException {
        File RESULT_FOLDER = new File("target/test-outputs", "merge");
        RESULT_FOLDER.mkdirs();

        File result = new File(RESULT_FOLDER, "GrandizerMerge-veryDense-A5.pdf");

        try (PdfWriter writer = new PdfWriter(new FileOutputStream(result)); PdfDocument pdfDocument = new PdfDocument(writer)) {
            PdfDenseMerger pdfMerger = new PdfDenseMerger(pdfDocument);
            pdfMerger.setPageSize(PageSize.A4).setTop(18).setBottom(18).setGap(5);

            String rootPath = "src/main/resources/result/";
            for (String resourceName : new String[]{"ColumnRendererAlternateAreaExample.pdf", "MoveColumnWithCustonRenderer.pdf"}) {

                PdfReader reader = new PdfReader(new File(rootPath.concat(resourceName)));
                PdfDocument sourceDocument = new PdfDocument(reader);
                pdfMerger.addPages(sourceDocument, 1, sourceDocument.getNumberOfPages());

            }
        }
    }

    public void testMerge() throws IOException {
        File result = new File(RESULT_FOLDER);

        PdfWriter writer = new PdfWriter(new FileOutputStream(result));
        PdfDocument novoPdfDocument = new PdfDocument(writer);

        PdfDenseMerger pdfMerger = new PdfDenseMerger(novoPdfDocument);
        pdfMerger.setTop(18).setBottom(18).setGap(5);

        PdfReader reader = new PdfReader(new File(DEST));
        PdfDocument sourceDocument = new PdfDocument(reader);
        pdfMerger.addPages(sourceDocument, 1, sourceDocument.getNumberOfPages());
        writer.close();
    }

    public void createPdf(String dest) throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));

        // Initialize document
        Document document = new Document(pdf, PageSize.A4);

        List<Materia> materias = new MateriaService().buildMateriaList(50, 10);

        LarguraPaginaEnum lastLayoutRendered = null;
        LarguraPaginaEnum currentLayout;
        boolean isFirstIteration = true;

        for (Iterator<Materia> iterator = materias.iterator(); iterator.hasNext(); ) {
            Materia materia = iterator.next();
            currentLayout = materia.getLarguraPagina();

            if (isFirstIteration) {
                document.setRenderer(LayoutColumn.getLayout(document, currentLayout));
                isFirstIteration = false;
            }

            if (lastLayoutRendered == null)
                lastLayoutRendered = currentLayout;

            if (currentLayoutHasChanged(lastLayoutRendered, currentLayout)) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                document.setRenderer(LayoutColumn.getLayout(document, currentLayout));
                document.add(new AreaBreak(AreaBreakType.LAST_PAGE));
                lastLayoutRendered = currentLayout;
            }

            addHtmlToDocument(document, materia);
        }

        document.close();
    }

    private void addHtmlToDocument(Document document, Materia materia) throws IOException {
        String htmlMateria = new TagAppender().loadHtmlMateriaContent(materia);

        List<IElement> iElements = HtmlConverter.convertToElements(htmlMateria, new ConverterProperties());

        for (IElement element : iElements)
            document.add((IBlockElement) element);

    }

    private boolean currentLayoutHasChanged(final LarguraPaginaEnum currentLayout, final LarguraPaginaEnum lastLayoutRenderer) {
        return lastLayoutRenderer != null &&
                !lastLayoutRenderer.equals(currentLayout);
    }

    /*Este método não está sendo usado porque a estratégia é setar a fonte via css nos templates.
    O código foi deixado aqui para facilitar caso seja necessário voltar a usar as fontes via
    properties.
    * */
    private void setFontPropertie(ConverterProperties properties) throws IOException {
        FontProvider fontProvider = new DefaultFontProvider();
        FontProgram fontProgram = FontProgramFactory.createFont(ARIAL_FONT);
        fontProvider.addFont(fontProgram);
        properties.setFontProvider(fontProvider);
    }
}
