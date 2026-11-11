// Clase con los datos de una película: id, título, director, año y género.
public class Pelicula {
    private int id;
    private String titulo;
    private String director;
    private int anio;
    private String genero;

    // Constructor con todos los datos de la película.
    public Pelicula(int id, String titulo, String director, int anio, String genero) {
        this.id = id;
        this.titulo = titulo;
        this.director = director;
        this.anio = anio;
        this.genero = genero;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDirector() {
        return director;
    }

    public int getAnio() {
        return anio;
    }

    public String getGenero() {
        return genero;
    }

    @Override

    // Formato con el que se muestra la película por pantalla.
    public String toString() {
        return "[" + id + "] " + titulo + " (" + anio + ") - Dir: " + director + " - Género: " + genero;
    }
}