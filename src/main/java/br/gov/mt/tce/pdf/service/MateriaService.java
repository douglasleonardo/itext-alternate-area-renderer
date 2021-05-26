package br.gov.mt.tce.pdf.service;

import br.gov.mt.tce.pdf.entity.Materia;
import br.gov.mt.tce.pdf.enumerados.LarguraPaginaEnum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MateriaService {

    public static final String SRC = "src/main/resources/text/materia_01.txt";

    /*public static void main(String args[]) throws IOException {
        new MateriaService().buildMateriaList(20, 6);
    }*/

    public List<Materia> buildMateriaList(final int total, final int rendererChangeInterval) {
        List<Materia> materias = null;
        int intervalo = 0;
        boolean isHasRendererInterval = rendererChangeInterval > 0;

        for (int i = 1; i <= total; i++) {
            boolean imprimePaginaInteira = false;

            if (materias == null)
                materias = new ArrayList<>();

            intervalo++;

            if (isHasRendererInterval &&
                    intervalo == rendererChangeInterval) {
                imprimePaginaInteira = true;
                intervalo = 0;
            }

            LarguraPaginaEnum larguraPagina = imprimePaginaInteira ? LarguraPaginaEnum.PAGINA_INTEIRA : LarguraPaginaEnum.COLUNA;
            String tituloMateria = i + " - EXTRATO DO TERMO DE COOPERAÇÃO TÉCNICA N° 084/2020/TCE/SEFAZ";
            String conteudoHTML = new MateriaService().read();

            materias.add(new Materia(tituloMateria, conteudoHTML, larguraPagina));
        }

        return total > 0 ? materias : Collections.emptyList();
    }

    private String read() {
        BufferedReader br;
        StringBuffer content = new StringBuffer();
        try {
            br = new BufferedReader(new FileReader(SRC));

            String line;

            while ((line = br.readLine()) != null) {
                content.append(line);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return content.toString();
    }


}
