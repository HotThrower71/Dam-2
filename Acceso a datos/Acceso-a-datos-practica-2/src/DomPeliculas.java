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

// Clase que hace todo lo relacionado con el XML usando JDOM2: leer, añadir, modificar, borrar y guardar.
public class DomPeliculas {
    // El XML cargado en memoria y la ruta del fichero.
    private Document documento;
    private String rutaArchivo;

    // Guarda la ruta y carga el XML nada más crear el objeto.
    public DomPeliculas(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        leerXml();
    }

    // Carga el fichero en memoria con SAXBuilder.
    // Si no existe o está mal formado se muestra el error y documento se queda a null.
    private void leerXml() {
        try {
            documento = new SAXBuilder().build(new File(this.rutaArchivo));
        } catch (FileNotFoundException e) {
            System.out.println("No se encuentra el archivo: " + rutaArchivo);
            e.printStackTrace();
        } catch (JDOMException e) {
            System.out.println("El XML está mal formado: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Devuelve el documento cargado.
    public Document getDocumento() {
        return documento;
    }

    // Recorre los <pelicula> de la raíz con getChildren y crea un objeto Pelicula por cada uno.
    // El id se lee con getAttributeValue y el resto con getChildText.
    public List<Pelicula> obtenerPeliculas() {
        List<Pelicula> listaPeliculas = new ArrayList<>();
        if (documento == null)
            return listaPeliculas;

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

    // Busca una película por su id. Si no la encuentra devuelve null.
    public Pelicula peliculaId(int id) {
        for (Pelicula p : obtenerPeliculas()) {
            if (id == p.getId()) {
                return p;
            }

        }
        return null;
    }

    // Calcula el id para una película nueva: el id más alto que haya más uno.
    public int siguienteId() {
        int maximo = 0;
        for (Pelicula p : obtenerPeliculas()) {
            if (p.getId() > maximo) {
                maximo = p.getId();
            }
        }
        return maximo + 1;
    }

    // Escribe el documento en el fichero con XMLOutputter y formato con sangría.
    // El FileWriter va dentro del try para que se cierre solo.
    public boolean guardarXML() {
        try (FileWriter escritor = new FileWriter(rutaArchivo)) {
            new XMLOutputter(Format.getPrettyFormat()).output(documento, escritor);
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Crea un nodo <pelicula> nuevo con new Element y addContent, lo mete en la raíz y guarda.
    // Si ya existe una película con ese id no la añade.
    public boolean añadirPelicula(Pelicula p) {
        if (p == null || documento == null) {
            return false;
        }
        if (peliculaId(p.getId()) != null) {
            return false;
        }

        Element nueva = new Element("pelicula");
        nueva.setAttribute("id", String.valueOf(p.getId()));
        nueva.addContent(new Element("nombre").setText(p.getTitulo()));
        nueva.addContent(new Element("director").setText(p.getDirector()));
        nueva.addContent(new Element("anio").setText(String.valueOf(p.getAnio())));
        nueva.addContent(new Element("genero").setText(p.getGenero()));
        documento.getRootElement().addContent(nueva);
        return guardarXML();
    }

    // Busca el nodo por su id y cambia el texto de sus hijos con getChild().setText().
    // Después guarda el fichero.
    public boolean modificarPelicula(Pelicula p) {
        if (p == null || documento == null) {
            return false;
        }

        for (Element e : documento.getRootElement().getChildren("pelicula")) {
            int id = Integer.parseInt(e.getAttributeValue("id"));
            if (id == p.getId()) {
                e.getChild("nombre").setText(p.getTitulo());
                e.getChild("director").setText(p.getDirector());
                e.getChild("anio").setText(String.valueOf(p.getAnio()));
                e.getChild("genero").setText(p.getGenero());
                return guardarXML();
            }
        }

        return false;
    }

    // Busca el nodo por su id, lo quita de la raíz con removeContent y guarda.
    public boolean eliminarPelicula(int id) {
        if (documento == null) {
            return false;
        }

        for (Element e : documento.getRootElement().getChildren("pelicula")) {
            if (Integer.parseInt(e.getAttributeValue("id")) == id) {
                documento.getRootElement().removeContent(e);
                return guardarXML();
            }
        }

        return false;
    }
}
