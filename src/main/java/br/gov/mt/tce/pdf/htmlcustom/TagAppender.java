package br.gov.mt.tce.pdf.htmlcustom;

import br.gov.mt.tce.pdf.entity.Materia;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;

public class TagAppender {

    private static final String FILE_PATH = "src/main/resources/pdf/fragments/conteudoMateria.html";

    private static final String TITULO_MATERIA = "title-materia";
    private static final String CONTEUDO_MATERIA = "div-content-materia";

    protected Document document;

    public TagAppender() throws IOException {
        this.document = Jsoup.parse(new File(FILE_PATH), "UTF-8");
    }

    /*public static void main(String[] args) throws IOException, ParserConfigurationException {
        MateriaService materiaService = new MateriaService();
        String s = new TagAppender().loadHtmlMateriaContent(materiaService.buildMateriaList(1, 0).get(0));
    }*/

    public String loadHtmlMateriaContent(Materia materia) throws IOException {
        //substitui o título no template html
        Element titulo = document.getElementById(TITULO_MATERIA);
        titulo.text(materia.getTitulo());

        //adicionando o conteúdo da matéria
        Element contentElement = document.getElementById(CONTEUDO_MATERIA);
        Document conteudoMateria = Jsoup.parseBodyFragment(materia.getHtmlMateria());

        for (Element e : conteudoMateria.body().children())
            contentElement.appendChild(e);

        return document.html();
    }
}
