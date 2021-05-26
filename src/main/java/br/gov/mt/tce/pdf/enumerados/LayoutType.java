package br.gov.mt.tce.pdf.enumerados;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LayoutType {
    COLUNA(1),
    PAGINA_INTEIRA(2);

    private Integer valor;
}
