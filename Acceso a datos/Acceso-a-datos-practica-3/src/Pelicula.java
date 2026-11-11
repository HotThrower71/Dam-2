/**
 * POJO de una película. Sin anotaciones de Jackson.
 * Jackson deduce el nombre de cada clave JSON a partir de los getters/setters,
 * por eso el año se llama getAño()/setAño(): así la clave es "año" como pide el enunciado.
 */
public class Pelicula {
    private int id;
    private String titulo;
    private String director;
    private int año;
    private String genero;

    // Jackson crea el objeto vacío y luego lo rellena con los setters
    public Pelicula() {
    }

    public Pelicula(int id, String titulo, String director, int año, String genero) {
        this.id = id;
        this.titulo = titulo;
        this.director = director;
        this.año = año;
        this.genero = genero;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + titulo + " (" + año + ") - Dir: " + director + " - Género: " + genero;
    }
}
