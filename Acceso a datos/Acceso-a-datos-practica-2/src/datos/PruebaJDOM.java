
package datos;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;


public class PruebaJDOM {
    public static void main(String[] args) {
        try {
            // 1. Crear un elemento raíz
            Element raiz = new Element("estado");
            raiz.setText("¡JDOM2 funciona correctamente!");

            // 2. Añadirlo a un nuevo documento
            Document documento = new Document(raiz);

            // 3. Imprimir el resultado por consola
            XMLOutputter outputter = new XMLOutputter();
            outputter.output(documento, System.out);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}