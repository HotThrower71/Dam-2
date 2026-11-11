import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// Catálogo de películas: lee las películas de un txt, las muestra y las pasa a XML con DOM.
public class Main {

    // Lee el fichero de texto línea a línea con BufferedReader.
    // Cada línea se separa por | con split y con los trozos se crea una Pelicula.
    // Se salta la cabecera y las líneas vacías.
    public static List<Pelicula> leerPeliculas(String nombreFichero) {
        List<Pelicula> peliculas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(nombreFichero))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.toUpperCase().startsWith("ID|")) {
                    continue;
                }

                String[] partes = linea.split("\\|");
                if (partes.length >= 5) {
                    int id = Integer.parseInt(partes[0].trim());
                    String titulo = partes[1].trim();
                    String director = partes[2].trim();
                    int anio = Integer.parseInt(partes[3].trim());
                    String genero = partes[4].trim();

                    peliculas.add(new Pelicula(id, titulo, director, anio, genero));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: No se encontró el archivo '" + nombreFichero + "'.");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error de formato numérico: " + e.getMessage());
        }

        return peliculas;
    }

    // Crea el XML con DOM. DocumentBuilder crea el documento vacío, createElement y appendChild
    // van montando los nodos, y el Transformer lo escribe en el fichero con sangría.
    public static void exportarXML(List<Pelicula> peliculas, String nombreFichero) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element catalogoElement = doc.createElement("catalogo");
            doc.appendChild(catalogoElement);

            for (Pelicula p : peliculas) {
                Element peliculaElement = doc.createElement("pelicula");
                peliculaElement.setAttribute("id", String.valueOf(p.getId()));

                Element tituloElement = doc.createElement("nombre");
                tituloElement.setTextContent(p.getTitulo());
                peliculaElement.appendChild(tituloElement);

                Element directorElement = doc.createElement("director");
                directorElement.setTextContent(p.getDirector());
                peliculaElement.appendChild(directorElement);

                Element anioElement = doc.createElement("anio");
                anioElement.setTextContent(String.valueOf(p.getAnio()));
                peliculaElement.appendChild(anioElement);

                Element generoElement = doc.createElement("genero");
                generoElement.setTextContent(p.getGenero());
                peliculaElement.appendChild(generoElement);

                catalogoElement.appendChild(peliculaElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(nombreFichero));
            transformer.transform(source, result);

            System.out.println("Archivo '" + nombreFichero + "' exportado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al exportar XML: " + e.getMessage());
        }
    }

    // Lee el XML con DOM (DocumentBuilder.parse) y vuelve a crear la lista de películas.
    // El id se saca del atributo y el resto de datos con getElementsByTagName.
    public static List<Pelicula> importarXML(String nombreFichero) {
        List<Pelicula> peliculas = new ArrayList<>();

        try {
            File archivo = new File(nombreFichero);
            if (!archivo.exists()) {
                System.err.println("Error: El archivo '" + nombreFichero + "' no existe. Exporta antes de importar.");
                return peliculas;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(archivo);
            doc.getDocumentElement().normalize();

            NodeList listaNodos = doc.getElementsByTagName("pelicula");

            for (int i = 0; i < listaNodos.getLength(); i++) {
                Node nodo = listaNodos.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;

                    int id = Integer.parseInt(elemento.getAttribute("id"));
                    String titulo = elemento.getElementsByTagName("nombre").item(0).getTextContent();
                    String director = elemento.getElementsByTagName("director").item(0).getTextContent();
                    int anio = Integer.parseInt(elemento.getElementsByTagName("anio").item(0).getTextContent());
                    String genero = elemento.getElementsByTagName("genero").item(0).getTextContent();

                    peliculas.add(new Pelicula(id, titulo, director, anio, genero));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al importar XML: " + e.getMessage());
        }

        return peliculas;
    }

    // Opción 1: muestra solo el título de cada película.
    private static void mostrarSoloTitulos(List<Pelicula> peliculas) {
        System.out.println("\n--- LISTA DE PELÍCULAS (SÓLO TÍTULOS) ---");
        for (Pelicula p : peliculas) {
            System.out.println("- " + p.getTitulo());
        }
    }

    // Opción 2: saca los géneros sin repetir, los enseña numerados y muestra
    // las películas del género que elija el usuario.
    private static void buscarPorGenero(List<Pelicula> peliculas, Scanner scanner) {
        List<String> generos = new ArrayList<>();
        for (Pelicula p : peliculas) {
            if (!generos.contains(p.getGenero())) {
                generos.add(p.getGenero());
            }
        }

        System.out.println("\n--- GÉNEROS DISPONIBLES ---");
        for (int i = 0; i < generos.size(); i++) {
            System.out.println((i + 1) + ". " + generos.get(i));
        }

        System.out.print("Selecciona un género por número: ");
        int seleccion;
        try {
            seleccion = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Opción no válida.");
            return;
        }

        if (seleccion < 1 || seleccion > generos.size()) {
            System.out.println("Número fuera de rango.");
            return;
        }

        String generoSeleccionado = generos.get(seleccion - 1);
        System.out.println("\nPelículas del género: " + generoSeleccionado);
        for (Pelicula p : peliculas) {
            if (p.getGenero().equalsIgnoreCase(generoSeleccionado)) {
                System.out.println(p);
            }
        }
    }

    // Opción 3: muestra las películas cuyo título contiene el texto escrito,
    // sin tener en cuenta mayúsculas y minúsculas.
    private static void buscarPorTitulo(List<Pelicula> peliculas, Scanner scanner) {
        System.out.print("\nIntroduce parte del título a buscar: ");
        String termino = scanner.nextLine().trim().toLowerCase();

        boolean coincidencias = false;
        System.out.println("\n--- RESULTADOS DE BÚSQUEDA ---");
        for (Pelicula p : peliculas) {
            if (p.getTitulo().toLowerCase().contains(termino)) {
                System.out.println(p);
                coincidencias = true;
            }
        }

        if (!coincidencias) {
            System.out.println("No se ha encontrado ninguna película que coincida con '" + termino + "'.");
        }
    }

    // Carga las películas del txt y muestra el menú hasta que se elige 0.
    // Lo que escribe el usuario se lee con Scanner.
    public static void main(String[] args) {
        String rutaTxt = "src/datos/peliculas.txt";
        String rutaXml = "peliculas.xml";

        List<Pelicula> peliculas = leerPeliculas(rutaTxt);

        if (peliculas.isEmpty()) {
            System.out.println("Aviso: No se encontraron películas en '" + rutaTxt + "'.");
        }

        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n===== CATÁLOGO =====");
            System.out.println("1. Mostrar todas");
            System.out.println("2. Buscar por género");
            System.out.println("3. Buscar por título");
            System.out.println("4. Exportar a XML");
            System.out.println("5. Importar XML");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, introduce un número válido.");
                continue;
            }

            switch (opcion) {
                case 1:
                    mostrarSoloTitulos(peliculas);
                    break;
                case 2:
                    buscarPorGenero(peliculas, scanner);
                    break;
                case 3:
                    buscarPorTitulo(peliculas, scanner);
                    break;
                case 4:
                    exportarXML(peliculas, rutaXml);
                    break;
                case 5:
                    List<Pelicula> peliculasImportadas = importarXML(rutaXml);
                    if (!peliculasImportadas.isEmpty()) {
                        System.out.println("\n--- PELÍCULAS RECUPERADAS DESDE " + rutaXml + " ---");
                        for (Pelicula p : peliculasImportadas) {
                            System.out.println(p);
                        }
                    }
                    break;
                case 0:
                    System.out.println("Programa finalizado.");
                    break;
                default:
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
                    break;
            }
        }

        scanner.close();
    }
}