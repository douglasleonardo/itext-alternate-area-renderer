package br.gov.mt.tce.pdf.entity;

import br.gov.mt.tce.pdf.enumerados.LarguraPaginaEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Materia {
    @ToString.Exclude
    private String titulo;

    @ToString.Exclude
    private String htmlMateria;
    private LarguraPaginaEnum larguraPagina;

    public Materia(String titulo, String htmlMateria, LarguraPaginaEnum larguraPagina) {
        this.titulo = titulo;
        this.htmlMateria = htmlMateria;
        this.larguraPagina = larguraPagina;
    }

    public boolean isColuna() {
        return larguraPagina.equals(LarguraPaginaEnum.COLUNA);
    }

    public boolean isPaginaInteira() {
        return larguraPagina.equals(LarguraPaginaEnum.PAGINA_INTEIRA);
    }
}
