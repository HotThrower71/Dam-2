import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.jdom2.JDOMException;

public class Main {
    private static final String RUTA_XML = "peliculas.xml";
    private static final String RUTA_JSON = "peliculas.json";

    private static final Scanner scanner = new Scanner(System.in);
    private static final LectorXml lector = new LectorXml(RUTA_XML);
    private static final GestorJson gestor = new GestorJson(RUTA_JSON);

    public static void main(String[] args) {
        int opcion = -1;

        while (opcion != 0) {
            mostrarMenu();

            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida: introduce un número del menú.");
                opcion = -1;
                continue;
            }

            switch (opcion) {
                case 1:
                    convertirXmlAJson();
                    break;
                case 2:
                    mostrarTodas();
                    break;
                case 3:
                    buscarPorId();
                    break;
                case 4:
                    anadirPelicula();
                    break;
                case 5:
                    modificarPelicula();
                    break;
                case 6:
                    eliminarPelicula();
                    break;
                case 0:
                    System.out.println("Programa finalizado.");
                    break;
                default:
                    System.out.println("Opción no válida. Vuelves al menú.");
                    break;
            }
        }
        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n========== CATÁLOGO DE PELÍCULAS ==========");
        System.out.println("1. Convertir XML a JSON");
        System.out.println("2. Mostrar todas las películas");
        System.out.println("3. Buscar una película por ID");
        System.out.println("4. Añadir una película");
        System.out.println("5. Modificar una película por ID");
        System.out.println("6. Eliminar una película por ID");
        System.out.println("0. Salir");
        System.out.println("==========================================");
        System.out.print("Selecciona una opción: ");
    }

    // ---------- Opción 1: migración XML -> JSON ----------
    private static void convertirXmlAJson() {
        if (!lector.existe()) {
            // Requisito: si no existe el XML, informar y detener el programa
            System.out.println("ERROR: no existe el fichero " + RUTA_XML + ". El programa se cierra.");
            System.exit(1);
        }
        try {
            List<Pelicula> peliculas = lector.leerPeliculas();
            Catalogo catalogo = new Catalogo();
            catalogo.setPeliculas(peliculas);
            gestor.guardar(catalogo);
            System.out.println("Migración completada: " + peliculas.size() + " películas guardadas en " + RUTA_JSON);
        } catch (JDOMException e) {
            System.out.println("El XML está mal formado: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("El XML tiene un id o un año que no es un número: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error de lectura/escritura: " + e.getMessage());
        }
    }

    // ---------- Carga del JSON (compartida por las opciones 2-6) ----------
    // Devuelve null si no se puede cargar (ya ha informado al usuario)
    private static Catalogo cargarCatalogo() {
        if (!gestor.existe()) {
            System.out.println("No existe " + RUTA_JSON + ". Ejecuta primero la opción 1 (Convertir XML a JSON).");
            return null;
        }
        try {
            Catalogo catalogo = gestor.cargar();
            if (catalogo.getPeliculas() == null) {
                System.out.println("El JSON no contiene la lista \"peliculas\".");
                return null;
            }
            return catalogo;
        } catch (IOException e) {
            System.out.println("Error al leer " + RUTA_JSON + ": " + e.getMessage());
            return null;
        }
    }

    // Guarda y avisa del resultado. Devuelve true si ha ido bien
    private static boolean guardarCatalogo(Catalogo catalogo) {
        try {
            gestor.guardar(catalogo);
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar " + RUTA_JSON + ": " + e.getMessage());
            return false;
        }
    }

    // ---------- Opción 2 ----------
    private static void mostrarTodas() {
        Catalogo catalogo = cargarCatalogo();
        if (catalogo == null) {
            return;
        }
        if (catalogo.getPeliculas().isEmpty()) {
            System.out.println("El catálogo está vacío.");
            return;
        }
        for (Pelicula p : catalogo.getPeliculas()) {
            System.out.println(p);
        }
    }

    // ---------- Opción 3 ----------
    private static void buscarPorId() {
        Catalogo catalogo = cargarCatalogo();
        if (catalogo == null) {
            return;
        }
        Integer id = pedirEntero("ID a buscar: ");
        if (id == null) {
            return;
        }
        Pelicula p = catalogo.buscarPorId(id);
        if (p == null) {
            System.out.println("No existe ninguna película con ID " + id);
        } else {
            System.out.println(p);
        }
    }

    // ---------- Opción 4 ----------
    private static void anadirPelicula() {
        Catalogo catalogo = cargarCatalogo();
        if (catalogo == null) {
            return;
        }
        // Si el usuario deja un campo mal, se repite SOLO ese paso (ver pedirTexto/pedirAño)
        String titulo = pedirTexto("Título: ");
        String director = pedirTexto("Director: ");
        int año = pedirAño("Año: ");
        String genero = pedirTexto("Género: ");

        int id = catalogo.siguienteId();
        Pelicula nueva = new Pelicula(id, titulo, director, año, genero);
        catalogo.getPeliculas().add(nueva);

        if (guardarCatalogo(catalogo)) {
            System.out.println("Película añadida con ID " + id + ": " + nueva);
        }
    }

    // ---------- Opción 5 ----------
    private static void modificarPelicula() {
        Catalogo catalogo = cargarCatalogo();
        if (catalogo == null) {
            return;
        }
        Integer id = pedirEntero("ID de la película a modificar: ");
        if (id == null) {
            return;
        }
        Pelicula p = catalogo.buscarPorId(id);
        if (p == null) {
            System.out.println("No existe ninguna película con ID " + id + ". No se ha cambiado nada.");
            return;
        }
        System.out.println("Datos actuales: " + p);
        System.out.println("(Pulsa ENTER sin escribir nada para mantener el valor actual)");

        // Se piden todos los datos antes de tocar el objeto: si algo falla, no queda a medias
        String titulo = pedirTextoOpcional("Nuevo título [" + p.getTitulo() + "]: ", p.getTitulo());
        String director = pedirTextoOpcional("Nuevo director [" + p.getDirector() + "]: ", p.getDirector());
        int año = pedirAñoOpcional("Nuevo año [" + p.getAño() + "]: ", p.getAño());
        String genero = pedirTextoOpcional("Nuevo género [" + p.getGenero() + "]: ", p.getGenero());

        p.setTitulo(titulo);
        p.setDirector(director);
        p.setAño(año);
        p.setGenero(genero);

        if (guardarCatalogo(catalogo)) {
            System.out.println("Película modificada: " + p);
        }
    }

    // ---------- Opción 6 ----------
    private static void eliminarPelicula() {
        Catalogo catalogo = cargarCatalogo();
        if (catalogo == null) {
            return;
        }
        Integer id = pedirEntero("ID de la película a eliminar: ");
        if (id == null) {
            return;
        }
        Pelicula p = catalogo.buscarPorId(id);
        if (p == null) {
            System.out.println("No existe ninguna película con ID " + id + ". No se ha cambiado nada.");
            return;
        }
        System.out.println("Se va a ELIMINAR: " + p);
        System.out.print("¿Seguro? (s/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("Eliminación cancelada.");
            return;
        }
        catalogo.eliminarPorId(id);
        if (guardarCatalogo(catalogo)) {
            System.out.println("Película eliminada (borrado físico del JSON).");
        }
    }

    // ---------- Entrada por consola ----------

    // Pide un entero una sola vez. Si no es número, informa y devuelve null (vuelve al menú)
    private static Integer pedirEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Tiene que ser un número entero. Vuelves al menú.");
            return null;
        }
    }

    // Repite la pregunta hasta que el texto no esté vacío
    private static String pedirTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("Este campo es obligatorio, no puede estar vacío.");
        }
    }

    // Repite la pregunta hasta que sea un año entero válido
    private static int pedirAño(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("El año tiene que ser un número entero.");
            }
        }
    }

    // ENTER vacío = mantener el valor actual
    private static String pedirTextoOpcional(String mensaje, String actual) {
        System.out.print(mensaje);
        String texto = scanner.nextLine().trim();
        return texto.isEmpty() ? actual : texto;
    }

    private static int pedirAñoOpcional(String mensaje, int actual) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (texto.isEmpty()) {
                return actual;
            }
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("El año tiene que ser un número entero.");
            }
        }
    }
}
