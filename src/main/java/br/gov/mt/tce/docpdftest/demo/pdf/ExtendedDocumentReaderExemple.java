package br.gov.mt.tce.docpdftest.demo.pdf;

import br.gov.mt.tce.docpdftest.demo.pdf.customrenderer.ExtendedDocumentRenderer;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.renderer.DocumentRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ExtendedDocumentReaderExemple {

    public static final String DEST = "src/main/resources/result/ExtendedDocumentReaderExemple.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new ExtendedDocumentReaderExemple().createPdf(DEST);
    }

    private void createPdf(String dest) throws FileNotFoundException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDocument);

        doc.add(newParagraph());
        RootLayoutArea endOfFullWidthContentArea = (RootLayoutArea) doc.getRenderer().getCurrentArea();

        ExtendedDocumentRenderer renderer1 = new ExtendedDocumentRenderer(doc,
                new RootLayoutArea(endOfFullWidthContentArea.getPageNumber(), endOfFullWidthContentArea.getBBox().clone().setWidth(200)));

        doc.setRightMargin(doc.getRightMargin() + doc.getPageEffectiveArea(PageSize.A4).getWidth() - 200);
        doc.setRenderer(renderer1);

        doc.add(newParagraph());

        ExtendedDocumentRenderer renderer2 = new ExtendedDocumentRenderer(doc,
                new RootLayoutArea(endOfFullWidthContentArea.getPageNumber(),
                        endOfFullWidthContentArea.getBBox().clone().moveRight(200)
                                .setWidth(endOfFullWidthContentArea.getBBox().getWidth() - 200)));
        doc.setRightMargin(36);
        doc.setLeftMargin(200 + 36);
        doc.setRenderer(renderer2);

        doc.add(newParagraph());

        RootLayoutArea areaColumn1 = (RootLayoutArea) renderer1.getCurrentArea();
        RootLayoutArea areaColumn2 = (RootLayoutArea) renderer2.getCurrentArea();

        RootLayoutArea downArea = areaColumn1.getPageNumber() > areaColumn2.getPageNumber() ? areaColumn1 :
                (areaColumn1.getPageNumber() < areaColumn2.getPageNumber() ? areaColumn2 :
                        (areaColumn1.getBBox().getTop() < areaColumn2.getBBox().getTop() ? areaColumn1 : areaColumn2));

        doc.setMargins(36, 36, 36, 36);
        DocumentRenderer renderer3 = new ExtendedDocumentRenderer(doc,
                new RootLayoutArea(downArea.getPageNumber(), downArea.getBBox().clone().setX(36).setWidth(doc.getPageEffectiveArea(PageSize.A4).getWidth())));
        doc.setRenderer(renderer3);

        doc.add(newParagraph());

        doc.close();

    }

    private Paragraph newParagraph() {
        Paragraph p = new Paragraph();
        p.setBorder(new SolidBorder(0.5f));
        for (int i = 1; i <= 500; i++) {
            p.add(new Text(i + " "));
        }

        return p;
    }

}
