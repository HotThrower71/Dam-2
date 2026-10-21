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

public class Main {
    public static void main(String[] args) {
        String rutaXml = "peliculas.xml";
        DomPeliculas dom = new DomPeliculas(rutaXml);

        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n===== CATÁLOGO =====");
            System.out.println("1. Mostrar todas las peliculas");
            System.out.println("2. Buscar pelicula por ID");
            System.out.println("3. Añadir una Pelicula");
            System.out.println("4. Modificar una pelicula por ID");
            System.out.println("5. Eliminar una pelicula por ID");
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
                    for (Pelicula p :dom.obtenerPeliculas())
                        System.out.println(p);
                    break;
                case 2:
                    System.out.print("Escribe el ID que quieras buscar ");
                    try {
                        Pelicula pelicula = dom.peliculaId(Integer.parseInt(scanner.nextLine()));
                        if (pelicula == null) {
                            System.out.println("No hay pelicula con ese ID");
                        } else {
                            System.out.println(pelicula);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("El ID tiene que ser un número.");
                    }
                    break;
                case 3:
                    try {
                        System.out.print("ID: ");
                        int id = Integer.parseInt(scanner.nextLine());

                        System.out.print("Título: ");
                        String titulo = scanner.nextLine();

                        System.out.print("Director: ");
                        String director = scanner.nextLine();

                        System.out.print("Año: ");
                        int anio = Integer.parseInt(scanner.nextLine());

                        System.out.print("Género: ");
                        String genero = scanner.nextLine();

                        Pelicula nueva = new Pelicula(id, titulo, director, anio, genero);

                        if (dom.añadirPelicula(nueva)) {
                            System.out.println("Película añadida: " + nueva);
                        } else {
                            System.out.println("No se ha podido añadir. ¿Ya existe una película con el ID " + id + "?");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("El ID y el año tienen que ser números.");
                    }
                    break;
                case 4:

                    break;
                case 5:

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