import java.util.ArrayList;
import java.util.List;

// Es lo que se guarda en el JSON: un objeto con la lista de películas, { "peliculas": [ ... ] }.
// Los métodos de ayuda no empiezan por get para que Jackson no los meta como claves en el JSON.
public class Catalogo {
    private List<Pelicula> peliculas;

    // Constructor vacío que deja la lista creada. Jackson también lo necesita.
    public Catalogo() {
        this.peliculas = new ArrayList<>();
    }

    // Getter y setter de la lista. Jackson los usa para la clave "peliculas".
    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    public void setPeliculas(List<Pelicula> peliculas) {
        this.peliculas = peliculas;
    }

    // Recorre la lista y devuelve la película con ese id, o null si no está.
    public Pelicula buscarPorId(int id) {
        for (Pelicula p : peliculas) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // Devuelve el id más alto más uno, o 1 si la lista está vacía.
    public int siguienteId() {
        int maximo = 0;
        for (Pelicula p : peliculas) {
            if (p.getId() > maximo) {
                maximo = p.getId();
            }
        }
        return maximo + 1;
    }

    // Quita de la lista la película con ese id. Devuelve true si la ha encontrado.
    public boolean eliminarPorId(int id) {
        Pelicula p = buscarPorId(id);
        if (p == null) {
            return false;
        }
        return peliculas.remove(p);
    }
}
