import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.Element;
import org.jdom2.output.Format;

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
        if (documento == null)
            return listaPeliculas; // si el XML no se pudo leer

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

    public Pelicula peliculaId(int id) {
        for (Pelicula p : obtenerPeliculas()) {
            if (id == p.getId()) {
                return p;
            }

        }
        return null;
    }

    public boolean guardarXML() {
        try {
            new XMLOutputter(Format.getPrettyFormat()).output(documento, new FileWriter(rutaArchivo));
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean añadirPelicula(Pelicula p){
if (p==null || documento==null){
    return false;
}
if (peliculaId(p.getId())!=null){
    return false;
}


        Element nueva = new Element("pelicula");
        nueva.setAttribute("id",String.valueOf(p.getId()));
        nueva.addContent(new Element("nombre").setText(p.getTitulo()));
        nueva.addContent(new Element("director").setText(p.getDirector()));
        nueva.addContent(new Element("anio").setText(String.valueOf(p.getAnio())));
        nueva.addContent(new Element("genero").setText(p.getGenero()));
        documento.getRootElement().addContent(nueva);
        return guardarXML();
    }
}
