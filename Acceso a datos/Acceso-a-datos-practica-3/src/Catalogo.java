import java.util.ArrayList;
import java.util.List;

/**
 * Objeto raíz del JSON: { "peliculas": [ ... ] }.
 * OJO: Jackson convierte en propiedad cualquier método getXxx() sin parámetros,
 * así que los métodos de ayuda de aquí NO se llaman get...
 */
public class Catalogo {
    private List<Pelicula> peliculas;

    public Catalogo() {
        this.peliculas = new ArrayList<>();
    }

    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    public void setPeliculas(List<Pelicula> peliculas) {
        this.peliculas = peliculas;
    }

    // Devuelve la película con ese ID o null si no existe
    public Pelicula buscarPorId(int id) {
        for (Pelicula p : peliculas) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // ID más alto + 1 (o 1 si el catálogo está vacío)
    public int siguienteId() {
        int maximo = 0;
        for (Pelicula p : peliculas) {
            if (p.getId() > maximo) {
                maximo = p.getId();
            }
        }
        return maximo + 1;
    }

    // Borrado físico. Devuelve true si existía y se ha quitado
    public boolean eliminarPorId(int id) {
        Pelicula p = buscarPorId(id);
        if (p == null) {
            return false;
        }
        return peliculas.remove(p);
    }
}
