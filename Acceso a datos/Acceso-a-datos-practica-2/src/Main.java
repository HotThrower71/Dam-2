import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String rutaXml = "peliculas.xml";
        DomPeliculas dom = new DomPeliculas(rutaXml);

        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n========== CATÁLOGO DE PELÍCULAS ==========");
            System.out.println("1. Mostrar todas las películas");
            System.out.println("2. Buscar una película por ID");
            System.out.println("3. Añadir una película");
            System.out.println("4. Modificar una película por ID");
            System.out.println("5. Eliminar una película por ID");
            System.out.println("0. Salir");
            System.out.println("==========================================");
            System.out.print("Selecciona una opción: ");

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
                        // El ID no se pide: es el más alto que existe + 1
                        int id = dom.siguienteId();

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
                            System.out.println("No se ha podido añadir la película.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("El año tiene que ser un número.");
                    }
                    break;
                case 4:
                    try {
                        System.out.print("ID: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        Pelicula pelicula=dom.peliculaId(id);
                        if (pelicula==null){
                            System.out.println("El id no existe");
                            break;
                        }
                        System.out.println("Datos actuales: " + pelicula);

                        System.out.print("Nuevo título: ");
                        String titulo = scanner.nextLine();

                        System.out.print("Nuevo director: ");
                        String director = scanner.nextLine();

                        System.out.print("Nuevo año: ");
                        int anio = Integer.parseInt(scanner.nextLine());

                        System.out.print("Nuevo género: ");
                        String genero = scanner.nextLine();

                        Pelicula modificada = new Pelicula(id, titulo, director, anio, genero);

                        if (dom.modificarPelicula(modificada)) {
                            System.out.println("Película modificada: " + modificada);
                        } else {
                            System.out.println("No se ha podido modificar.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("El ID y el año tienen que ser números.");
                    } 
                    break;
                case 5:
                    try {
                        System.out.print("ID de la película a eliminar: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        Pelicula pelicula = dom.peliculaId(id);
                        if (pelicula == null) {
                            System.out.println("El id no existe");
                            break;
                        }
                        System.out.println("Vas a eliminar: " + pelicula);

                        System.out.print("¿Seguro que quieres eliminarla? (s/n): ");
                        String respuesta = scanner.nextLine();

                        if (respuesta.equalsIgnoreCase("s")) {
                            if (dom.eliminarPelicula(id)) {
                                System.out.println("Película eliminada (borrado físico: se ha quitado del XML).");
                            } else {
                                System.out.println("No se ha podido eliminar.");
                            }
                        } else {
                            System.out.println("Eliminación cancelada.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("El ID tiene que ser un número.");
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