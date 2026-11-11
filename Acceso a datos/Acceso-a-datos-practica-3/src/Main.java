import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.jdom2.JDOMException;

// Menú del catálogo: la opción 1 pasa el XML a JSON y el resto son el CRUD sobre el JSON.
public class Main {
    private static final String RUTA_XML = "peliculas.xml";
    private static final String RUTA_JSON = "peliculas.json";

    private static final Scanner scanner = new Scanner(System.in);
    private static final LectorXml lector = new LectorXml(RUTA_XML);
    private static final GestorJson gestor = new GestorJson(RUTA_JSON);

    // Muestra el menú hasta que se elige 0 y llama al método de cada opción.
    // Si no se escribe un número se captura NumberFormatException y se vuelve a mostrar el menú.
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

    // Imprime las opciones del menú.
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

    // Opción 1: lee las películas del XML con LectorXml, las mete en un Catalogo
    // y lo guarda en JSON con GestorJson. Si no existe el XML, el programa se cierra.
    private static void convertirXmlAJson() {
        if (!lector.existe()) {
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

    // Carga el JSON para las opciones 2 a 6.
    // Si el fichero no existe o no se puede leer, muestra el error y devuelve null.
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

    // Guarda el catálogo en el JSON. Devuelve false y muestra el error si no se ha podido.
    private static boolean guardarCatalogo(Catalogo catalogo) {
        try {
            gestor.guardar(catalogo);
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar " + RUTA_JSON + ": " + e.getMessage());
            return false;
        }
    }

    // Opción 2: muestra todas las películas del JSON.
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

    // Opción 3: pide un id y muestra la película con buscarPorId.
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

    // Opción 4: pide los datos, calcula el id con siguienteId, añade la película y guarda el JSON.
    // Si un dato está mal solo se vuelve a pedir ese.
    private static void anadirPelicula() {
        Catalogo catalogo = cargarCatalogo();
        if (catalogo == null) {
            return;
        }
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

    // Opción 5: busca la película por id y pide los datos nuevos.
    // Si se pulsa ENTER sin escribir nada se queda el valor que tenía.
    // Se piden todos los datos antes de cambiar nada y luego se aplican con los setters.
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

    // Opción 6: busca la película por id, pide confirmación y la quita de la lista con eliminarPorId.
    // Después guarda el JSON.
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

    // Pide un número una vez. Si no es un número avisa y devuelve null para volver al menú.
    private static Integer pedirEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Tiene que ser un número entero. Vuelves al menú.");
            return null;
        }
    }

    // Pide un texto y lo repite hasta que no esté vacío.
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

    // Pide el año y lo repite hasta que sea un número.
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

    // Pide un texto. Si se deja vacío devuelve el valor actual.
    private static String pedirTextoOpcional(String mensaje, String actual) {
        System.out.print(mensaje);
        String texto = scanner.nextLine().trim();
        return texto.isEmpty() ? actual : texto;
    }

    // Pide el año. Si se deja vacío devuelve el actual y si no es un número lo vuelve a pedir.
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
