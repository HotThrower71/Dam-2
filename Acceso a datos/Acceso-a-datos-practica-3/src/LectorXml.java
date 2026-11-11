import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Lee el peliculas.xml de la práctica anterior con JDOM2.
 * Solo se usa en la migración inicial (opción 1 del menú).
 */
public class LectorXml {
    private String rutaArchivo;

    public LectorXml(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public boolean existe() {
        return new File(rutaArchivo).exists();
    }

    // Devuelve todas las películas del XML. Lanza excepción si no se puede leer o está mal formado
    public List<Pelicula> leerPeliculas() throws IOException, JDOMException {
        Document documento = new SAXBuilder().build(new File(rutaArchivo));
        List<Pelicula> lista = new ArrayList<>();

        for (Element e : documento.getRootElement().getChildren("pelicula")) {
            int id = Integer.parseInt(e.getAttributeValue("id"));
            String titulo = e.getChildText("nombre");
            String director = e.getChildText("director");
            int año = Integer.parseInt(e.getChildText("anio"));
            String genero = e.getChildText("genero");
            lista.add(new Pelicula(id, titulo, director, año, genero));
        }
        return lista;
    }
}
