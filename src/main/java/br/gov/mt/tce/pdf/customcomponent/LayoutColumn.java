package br.gov.mt.tce.pdf.customcomponent;

import br.gov.mt.tce.pdf.enumerados.LarguraPaginaEnum;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Document;

public class LayoutColumn {
    protected float offSet = 36;
    protected float gutter = 10;
    protected float columnWidth = (PageSize.A4.getWidth() - offSet * 2) / 2 - gutter;
    protected float columnHeight = PageSize.A4.getHeight() - offSet * 2;

    public static CustomColumnRenderer getLayout(Document document, final LarguraPaginaEnum larguraPaginaEnum) {
        if (larguraPaginaEnum.isColuna())
            return createLayoutColumns(document);
        else
            return createLayoutFullPage(document);
    }

    public static CustomColumnRenderer createLayoutColumns(Document document) {
        LayoutColumn layout = new LayoutColumn();

        Rectangle[] columns = {
                new Rectangle(layout.offSet, layout.offSet, layout.columnWidth, layout.columnHeight),
                new Rectangle(layout.offSet + layout.columnWidth + layout.gutter, layout.offSet, layout.columnWidth, layout.columnHeight)};

        return new CustomColumnRenderer(document, columns);
    }

    //TODO - validar dimensionamento da coluna unica
    public static CustomColumnRenderer createLayoutFullPage(Document document) {
        LayoutColumn layout = new LayoutColumn();

        float columnWidthFullPage = PageSize.A4.getWidth() - layout.offSet * 2;
        float columnHeightFullPage = PageSize.A4.getHeight() - layout.offSet * 2;

        Rectangle[] columns = {
                new Rectangle(layout.offSet, layout.offSet, columnWidthFullPage, columnHeightFullPage)
        };

        return new CustomColumnRenderer(document, columns);
    }
}