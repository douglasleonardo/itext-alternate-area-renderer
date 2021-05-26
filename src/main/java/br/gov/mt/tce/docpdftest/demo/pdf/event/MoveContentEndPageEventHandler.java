package br.gov.mt.tce.docpdftest.demo.pdf.event;

import br.gov.mt.tce.docpdftest.demo.pdf.customrenderer.ColumnRendererAlternateArea;
import br.gov.mt.tce.docpdftest.demo.pdf.service.PrinterContent;
import br.gov.mt.tce.pdf.entity.Materia;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
public class MoveContentEndPageEventHandler implements IEventHandler {

    @Getter
    @Setter
    private ColumnRendererAlternateArea temporaryRenderer;

    private ColumnRendererAlternateArea finalRenderer;

    @Getter
    @Setter
    private List<Materia> materias;

    public MoveContentEndPageEventHandler(ColumnRendererAlternateArea finalRenderer) {
        this.finalRenderer = finalRenderer;
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdf = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        int previousPageNumber = pdf.getPageNumber(page) - 1;

        if (previousPageNumber == 0)
            return;

//        float height = finalRenderer.getCurrentArea().getBBox().getHeight();
        float height = finalRenderer.getNextContentHeight() + 3;

        //na quebra preciso saber o tamanho total do conteúdo que será impresso na próxima página (parte da 25, 26 e a 27) e a ordem atual de impressão
        //E o que eu sei até agora? Só sei o tamnho do conteúdo do overflowRenderer que é exatamente a proxima impressão.
        if (finalRenderer.isLastContent() &&
                finalRenderer.getWrittenDirection().isColuna()) {
            height = height * 2;
//            height = finalRenderer.getNextContentHeight() > height ? finalRenderer.getNextContentHeight() : (height / 2) + 3;
        }

        if (!finalRenderer.isLastContent())
            finalRenderer.setContentHeight(height);

        finalRenderer.setOccupiedAreaHeight(0F);
        //finalRenderer.setNextOccupiedAreaHeight(0F);
        finalRenderer.setNextContentHeight(0F);
        finalRenderer.setTotalContentHeight(0F);
    }
}
