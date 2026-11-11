import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Lee y escribe el fichero peliculas.json con Jackson (ObjectMapper).
public class GestorJson {
    private String rutaArchivo;
    private ObjectMapper mapper = new ObjectMapper();

    // Guarda la ruta del JSON.
    public GestorJson(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    // Comprueba si el fichero JSON existe.
    public boolean existe() {
        return new File(rutaArchivo).exists();
    }

    // Lee el JSON y lo convierte en un objeto Catalogo con readValue.
    public Catalogo cargar() throws IOException {
        return mapper.readValue(new File(rutaArchivo), Catalogo.class);
    }

    // Pasa el catálogo a JSON con sangría (writerWithDefaultPrettyPrinter) y lo escribe en el fichero.
    // Primero se convierte a bytes y después se escribe, así si falla la conversión
    // no se estropea el fichero que ya había.
    public void guardar(Catalogo catalogo) throws IOException {
        byte[] json;
        try {
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(catalogo);
        } catch (JsonProcessingException e) {
            throw new IOException("No se pudo convertir el catálogo a JSON: " + e.getOriginalMessage(), e);
        }
        Files.write(new File(rutaArchivo).toPath(), json);
    }
}
