package br.gov.mt.tce.doc.pdf.config;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;

public class LayoutConfig {

    public static final PageSize PAGE_SIZE = PageSize.A4;
    public static final float MARGIN_TOP = 130.5F;
    public static final float MARGIN_RIGHT = 26.64F;
    public static final float MARGIN_BOTTOM = 48.96F;
    public static final float MARGIN_LEFT = 26.64F;

    public static final float COLUMN_PADDING = 8.64F;//10.0F;
    public static final float GUNTTER = 8.64F;

    public static final float HEADER_HORIZONTAL_OFFSET = MARGIN_LEFT + MARGIN_RIGHT;
    public static final float HEADER_VERTICAL_OFFSET = MARGIN_TOP + MARGIN_BOTTOM;

    public static final float HEADER_COLUMN_WIDTH = PAGE_SIZE.getWidth() - HEADER_HORIZONTAL_OFFSET - COLUMN_PADDING;
    public static final float HEADER_COLUMN_HEIGHT = PAGE_SIZE.getHeight() - HEADER_VERTICAL_OFFSET;
    public static final float HEADER_COLUMN_X = MARGIN_LEFT;

    public static final Rectangle PAGE_COLUMN = new Rectangle(HEADER_COLUMN_X, MARGIN_BOTTOM, HEADER_COLUMN_WIDTH, HEADER_COLUMN_HEIGHT);

    public static final float BODY_HORIZONTAL_OFFSET = MARGIN_LEFT + MARGIN_RIGHT;
    public static final float BODY_VERTICAL_OFFSET = MARGIN_TOP + MARGIN_BOTTOM;
    public static final float BODY_COLUMN_WIDTH = ((PAGE_SIZE.getWidth() - BODY_HORIZONTAL_OFFSET) / 2) - COLUMN_PADDING;
    public static final float BODY_COLUMN_HEIGHT = PAGE_SIZE.getHeight() - BODY_VERTICAL_OFFSET;
    public static final float BODY_COLUMN_X = MARGIN_LEFT;

    public static final Rectangle[] COLUMNS = {
            new Rectangle(BODY_COLUMN_X, MARGIN_BOTTOM, BODY_COLUMN_WIDTH, BODY_COLUMN_HEIGHT),
            new Rectangle(BODY_COLUMN_X + BODY_COLUMN_WIDTH + GUNTTER, MARGIN_BOTTOM, BODY_COLUMN_WIDTH, BODY_COLUMN_HEIGHT)
    };
}
