import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Toda la lectura y escritura del JSON pasa por aquí, siempre con Jackson (ObjectMapper).
 */
public class GestorJson {
    private String rutaArchivo;
    private ObjectMapper mapper = new ObjectMapper();

    public GestorJson(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public boolean existe() {
        return new File(rutaArchivo).exists();
    }

    // JSON -> objetos Java
    public Catalogo cargar() throws IOException {
        return mapper.readValue(new File(rutaArchivo), Catalogo.class);
    }

    // Objetos Java -> JSON con sangría.
    // Primero se serializa en memoria: si Jackson falla, el fichero actual no se toca.
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
