package br.gov.mt.tce.pdftest;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.TableRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DelayedLayout {
    public static String DEST = "target/output/StackOverflow/DelayedLayout/delayed.pdf";

    public static void main(String[] args) throws IOException, FileNotFoundException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new DelayedLayout().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException, FileNotFoundException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        boolean immediateFlush = false;
        boolean relayout = true;
        //Set immediate layout to false, so the document doesn't immediatly write render-results to its outputstream
        Document doc = new Document(pdfDoc, PageSize.A4, immediateFlush);
        Table tOne = createSimpleTable();
        for (int i = 0; i < 5; i++) {
            //Add a table and some whitespace
            doc.add(tOne);

            doc.add(new Paragraph(""));

        }
        System.out.println("\nInitial layout results");
        printOccupiedAreasOfTableRenderers(doc.getRenderer());
        System.out.println("\nAdding extra cells to the table");
        addToTable(tOne);
        printOccupiedAreasOfTableRenderers(doc.getRenderer());
        System.out.println("\nForcing the document to redo the layout");
        if (relayout)
            doc.relayout();

        printOccupiedAreasOfTableRenderers(doc.getRenderer());
        doc.close();
    }

    /**
     * Create a very simple table
     *
     * @return simple table
     */
    private Table createSimpleTable() {
        int nrOfCols = 3;
        int nrOfRows = 5;

        Table res = new Table(nrOfCols);
        for (int i = 0; i < nrOfRows; i++) {
            for (int j = 0; j < nrOfCols; j++) {
                Cell c = new Cell();
                c.add(new Paragraph("[" + i + ", " + j + "]"));
                res.addCell(c);
            }
        }

        return res;
    }

    /**
     * Add some extra cells to an exisiting table
     *
     * @param tab table to add cells to
     */
    private void addToTable(Table tab) {
        int nrOfRows = 5;
        int nrOfCols = tab.getNumberOfColumns();
        for (int i = 0; i < nrOfRows * nrOfCols; i++) {
            Cell c = new Cell();
            c.add(new Paragraph("Extra cell" + i));
            tab.addCell(c);
        }

    }

    /**
     * Recursively iterate over the renderer tree, writing the occupied area to the console
     *
     * @param currentNode current renderer-node to check
     */
    private void printOccupiedAreasOfTableRenderers(IRenderer currentNode) {
        if (currentNode.getClass().equals(TableRenderer.class)) {
            System.out.println("Table renderer with occupied area: " + currentNode.getOccupiedArea());
        }
        for (IRenderer child : currentNode.getChildRenderers()) {
            printOccupiedAreasOfTableRenderers(child);
        }
    }
}