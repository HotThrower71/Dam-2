package test.view;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import test.support.Reflect;
import view.DetailPanel;

// El panel grande tiene que mostrar el resumen, los recursos y cada hilo
class DetailPanelTest {

    @Test
    void muestraElResumenLosRecursosYLosHilos() {
        DetailPanel panel = new DetailPanel();
        List<String> resources = new ArrayList<>();
        resources.add("Recurso A: stock 4  (máx 5, mín 0)");
        List<String> threads = new ArrayList<>();
        threads.add("Productor 1 (fijo, Recurso A)  ->  producidos: 12");
        threads.add("Consumidor 1 (fijo, Recurso A)  ->  consumidos: 8");

        panel.update(true, 2, 1, 1, 0, 12, 8, resources, threads);

        String text = Reflect.areaText(panel);
        assertTrue(text.contains("Estado: En marcha"), text);
        assertTrue(text.contains("Productores esperando: 1 de 2"), text);
        assertTrue(text.contains("Consumidores esperando: 0 de 1"), text);
        assertTrue(text.contains("Total producido: 12"), text);
        assertTrue(text.contains("Total consumido: 8"), text);
        assertTrue(text.contains("Recurso A: stock 4"), text);
        assertTrue(text.contains("Productor 1 (fijo, Recurso A)"), text);
        assertTrue(text.contains("Consumidor 1 (fijo, Recurso A)"), text);
    }

    @Test
    void siEstaParadoLoDice() {
        DetailPanel panel = new DetailPanel();

        panel.update(false, 0, 0, 0, 0, 0, 0, new ArrayList<String>(), new ArrayList<String>());

        assertTrue(Reflect.areaText(panel).contains("Estado: Parada"));
    }

    @Test
    void cadaActualizacionSustituyeAlTextoAnterior() {
        DetailPanel panel = new DetailPanel();
        panel.update(true, 1, 1, 0, 0, 5, 5, new ArrayList<String>(), new ArrayList<String>());

        panel.update(true, 1, 1, 0, 0, 9, 9, new ArrayList<String>(), new ArrayList<String>());

        String text = Reflect.areaText(panel);
        assertTrue(text.contains("Total producido: 9"), text);
        assertTrue(!text.contains("Total producido: 5"), text);
    }
}
