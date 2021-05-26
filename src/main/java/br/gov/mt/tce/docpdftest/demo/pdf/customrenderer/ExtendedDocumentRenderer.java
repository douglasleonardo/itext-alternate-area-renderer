package br.gov.mt.tce.docpdftest.demo.pdf.customrenderer;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.renderer.DocumentRenderer;

public class ExtendedDocumentRenderer extends DocumentRenderer {

    public ExtendedDocumentRenderer(Document document, RootLayoutArea currentArea) {
        super(document);
        this.currentArea = new RootLayoutArea(currentArea.getPageNumber(), currentArea.getBBox().clone());
        this.currentPageNumber = this.currentArea.getPageNumber();
    }
}

