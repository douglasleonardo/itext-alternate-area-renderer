package br.gov.mt.tce.docpdftest.demo.pdf.customrenderer;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.IRenderer;

import java.util.HashSet;
import java.util.Set;

public class MyColumnRenderer extends DocumentRenderer {

    protected final Rectangle[] columns;
    protected int nextAreaNumber;
    protected int currentAreaNumber;
    protected Set<Integer> moveColumn = new HashSet<>();

    public MyColumnRenderer(Document document, Rectangle[] columns) {
        super(document, false);
        this.columns = columns;
    }

    @Override
    protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
        if (overflowResult != null
                && overflowResult.getAreaBreak() != null
                && overflowResult.getAreaBreak().getType()
                != AreaBreakType.NEXT_AREA) {
            nextAreaNumber = 0;
        }
        if (nextAreaNumber % columns.length == 0) {
            super.updateCurrentArea(overflowResult);
        }
        currentAreaNumber = nextAreaNumber + 1;
        return (currentArea = new RootLayoutArea(currentPageNumber, columns[nextAreaNumber++ % columns.length].clone()));
    }

    @Override
    protected PageSize addNewPage(PageSize customPageSize) {
        if (currentAreaNumber != nextAreaNumber
                && currentAreaNumber % columns.length != 0)
            moveColumn.add(currentPageNumber - 1);
        return super.addNewPage(customPageSize);
    }

    @Override
    protected void flushSingleRenderer(IRenderer resultRenderer) {
        int pageNum = resultRenderer.getOccupiedArea().getPageNumber();
        if (moveColumn.contains(pageNum)) {
            resultRenderer.move(columns[0].getWidth() / 2, 0);
        }
        super.flushSingleRenderer(resultRenderer);
    }
}
