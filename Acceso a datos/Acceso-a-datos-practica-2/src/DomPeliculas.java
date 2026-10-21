import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Element;

import java.util.List;
import java.util.ArrayList;

public class DomPeliculas {
    private Document documento; // Guarda el XML entero en la memoria
    private String rutaArchivo; // Guarda donde esta el archivo

    public DomPeliculas(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        leerXml();
    }

    private void leerXml() {
        try {
            documento = new SAXBuilder().build(new File(this.rutaArchivo));
        } catch (FileNotFoundException e) {
            System.out.println("No se encuentra el archivo: " + rutaArchivo);
        } catch (JDOMException e) {
            System.out.println("El XML está mal formado: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public Document getDocumento() {
        return documento;
    }

    public List<Pelicula> obtenerPeliculas() {
    List<Pelicula> listaPeliculas = new ArrayList<>();
    if (documento == null) return listaPeliculas; // si el XML no se pudo leer

    for (Element e : documento.getRootElement().getChildren("pelicula")) {
        int id = Integer.parseInt(e.getAttributeValue("id"));
        String nombre = e.getChildText("nombre");
        String director = e.getChildText("director");
        int anio = Integer.parseInt(e.getChildText("anio"));
        String genero = e.getChildText("genero");
        listaPeliculas.add(new Pelicula(id, nombre, director, anio, genero));
    }
    return listaPeliculas;
}

public Pelicula peliculaId(int id ){
    for (Pelicula p :obtenerPeliculas()){
        if (id==p.getId()){
            return p;
        }
        
    }
    return null;
}
}
