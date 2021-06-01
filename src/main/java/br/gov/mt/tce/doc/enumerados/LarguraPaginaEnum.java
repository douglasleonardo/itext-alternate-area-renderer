package br.gov.mt.tce.doc.enumerados;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LarguraPaginaEnum {
    COLUNA("N", "Coluna"),
    PAGINA_INTEIRA("S", "Página inteira");

    private String valor;
    private String descricao;

    public boolean isColuna() {
        return this.equals(LarguraPaginaEnum.COLUNA);
    }

    public boolean isPaginaInteira() {
        return this.equals(LarguraPaginaEnum.PAGINA_INTEIRA);
    }
}
