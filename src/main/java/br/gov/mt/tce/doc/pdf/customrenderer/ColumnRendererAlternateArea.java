package br.gov.mt.tce.doc.pdf.customrenderer;

import br.gov.mt.tce.doc.pdf.config.LayoutConfig;
import br.gov.mt.tce.doc.enumerados.LarguraPaginaEnum;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

public class ColumnRendererAlternateArea extends DocumentRenderer {
    protected final Rectangle pageColumns;
    protected final Rectangle[] columns;

    protected int nextAreaNumber;
    protected int currentAreaNumber;

    protected HashSet<Integer> moveColumn = new HashSet();

    @Getter
    @Setter
    private float contentHeight = 0F;

    @Getter
    @Setter
    private float totalContentHeight;

    @Getter
    @Setter
    private float occupiedAreaHeight = 0F;

    @Setter
    @Getter
    private LarguraPaginaEnum writtenDirection;

    @Getter
    @Setter
    private float nextContentHeight = 0F;

    @Getter
    @Setter
    private boolean lastContent;

    @Getter
    @Setter
    private boolean temporaryRenderer = false;

    public ColumnRendererAlternateArea(Document document, Rectangle pageColumn, Rectangle[] columns) {
        super(document, false);
        this.pageColumns = pageColumn;
        this.columns = columns;
    }

    public Document getDocument() {
        return this.document;
    }

    @Override
    protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {

        if (isTemporaryRenderer()) {
            return updateTemporaryRenderer(overflowResult);

        } else {
            return updateFinalRenderer(overflowResult);
        }
    }

    private LayoutArea updateTemporaryRenderer(LayoutResult overflowResult) {
        if (writtenDirection.isColuna()) {
            if (this.nextAreaNumber % this.columns.length == 0)
                super.updateCurrentArea(overflowResult);

            return this.currentArea = new RootLayoutArea(this.currentPageNumber, this.columns[this.nextAreaNumber++ % this.columns.length].clone());

        } else if (isFullPage())
            super.updateCurrentArea(overflowResult);

        return (currentArea = new RootLayoutArea(currentPageNumber, pageColumns.clone()));
    }

    private LayoutArea updateFinalRenderer(LayoutResult overflowResult) {
        if (writtenDirection.isColuna()) {

            if (currentAreaNumber == 0 || (isFullPage() && isFirstColumnArea()))
                super.updateCurrentArea(overflowResult);

            if (isFirstColumnArea() && totalContentHeight > getFreePageSpace())
                calculateNextContentHeight();


            if (isFirstColumnArea()) {
                occupiedAreaHeight += contentHeight;
            }

            currentAreaNumber = nextAreaNumber + 1;
            Rectangle newColumn = columns[nextAreaNumber++ % columns.length].clone();
            if (contentHeight > 0) {
                newColumn.setHeight(contentHeight);

                /*
                 * posiciona o início do retangulo no eixo Y
                 * A impressão considera os eixos X e Y e se inicia nos pontos X=0, Y=0 de baixo para cima.
                 */
                newColumn.setY((PageSize.A4.getHeight() - LayoutConfig.MARGIN_TOP) - occupiedAreaHeight);
            }

            return (currentArea = new RootLayoutArea(currentPageNumber, newColumn));

        } else {
            //verifica se a nextOccupiedAreaHeight é maior que o tamanho disponível da página
            if (contentHeight > getFreePageSpace())
                calculateNextContentHeight();

            if (isFullPage())
                super.updateCurrentArea(overflowResult);

            occupiedAreaHeight += contentHeight;

            Rectangle newArea = pageColumns.clone();

            if (contentHeight > 0) {
                newArea.setHeight(contentHeight);
                newArea.setY((PageSize.A4.getHeight() - LayoutConfig.MARGIN_TOP) - occupiedAreaHeight);
            }

            return (currentArea = new RootLayoutArea(currentPageNumber, newArea));
        }

    }

    private void calculateNextContentHeight() {
        if (nextContentHeight <= 0F)
            nextContentHeight = contentHeight - getFreePageSpace();

        contentHeight = getFreePageSpace();
    }

    private boolean isFirstColumnArea() {
        return currentAreaNumber % columns.length == 0;
    }

    /**
     * Calcula altura da coluna para dividir o conteúdo de uma matéria em duas partes iguais
     *
     * @return
     */
    public float calculateColumnHeightToSplitContent() {
        if (currentArea == null || currentArea.getBBox() == null)
            return 0F;

        float totalHeightDefaultColumn = columns[0].getHeight();
        float avaliableArea = currentArea.getBBox().getHeight();

        if (isFirstColumnArea())
            totalHeightDefaultColumn = totalHeightDefaultColumn * 2;

        return ((totalHeightDefaultColumn - avaliableArea) / 2); //TODO - tirei o + 3 daqui...
    }

    public float getOccupedPageArea() {
        if (currentArea == null || currentArea.getBBox() == null)
            return 0F;

        float totalHeightDefaultColumn = pageColumns.getHeight();
        float avaliableArea = currentArea.getBBox().getHeight();

        return totalHeightDefaultColumn - avaliableArea;
    }

    private boolean isFullPage() {
        return LayoutConfig.BODY_COLUMN_HEIGHT - occupiedAreaHeight <= 0 /*<= 3*/;
    }

    private float getFreePageSpace() {
        float freeSpaceOnPage = LayoutConfig.BODY_COLUMN_HEIGHT - occupiedAreaHeight;
        return freeSpaceOnPage > 0 ? freeSpaceOnPage : 0F;
    }

    public float sumChildsHeight() {
        float totalHeight = 0F;

        if (this.getChildRenderers() == null || this.getChildRenderers().isEmpty())
            return totalHeight;

        for (IRenderer renderer : this.getChildRenderers()) {
            if (renderer.getOccupiedArea() != null)
                totalHeight += renderer.getOccupiedArea().getBBox().getHeight();
        }

        return totalHeight;
    }

    /*@Override
    protected PageSize addNewPage(PageSize customPageSize) {
        return super.addNewPage(customPageSize);
    }

    @Override
    protected void flushSingleRenderer(IRenderer resultRenderer) {
        int pageNum = resultRenderer.getOccupiedArea().getPageNumber();
        if (moveColumn.contains(pageNum)) {
            if (writtenDirection.isColuna()) {
                resultRenderer.move(columns[0].getWidth() / 2, 0);
            } else {
                resultRenderer.move(pageColumns.getWidth() / 2, 0);
            }
        }
        super.flushSingleRenderer(resultRenderer);
    }*/
}
