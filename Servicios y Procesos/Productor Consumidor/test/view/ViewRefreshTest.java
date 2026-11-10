package test.view;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import test.support.FakePresenter;
import test.support.Reflect;
import view.View;
import view.listeners.RefreshListener;

// Estas pruebas crean una ventana (JFrame, sin mostrarla), asi que se saltan si no hay pantalla
class ViewRefreshTest {

    @Test
    void elListenerDeRefrescoHaceQueLaVistaPidaLosDatosAlPresenter() {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "no hay pantalla");
        View view = new View();
        FakePresenter presenter = new FakePresenter();
        view.setPresenter(presenter); // aplica los ajustes por defecto
        int before = presenter.refreshes;

        new RefreshListener(view).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "tick"));

        assertTrue(presenter.refreshes > before, "refresh() debe consultar isRunning() al presenter");
    }

    @Test
    void refrescarPintaLosDatosEnElPanelGrande() {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), "no hay pantalla");
        View view = new View();
        FakePresenter presenter = new FakePresenter();
        presenter.running = true;
        presenter.resourcesInfo.add("Recurso A: stock 1");
        presenter.threadsInfo.add("Productor 1 (fijo, Recurso A)");
        view.setPresenter(presenter);

        view.refresh();

        // El panel de detalle es privado en View: se comprueba por reflexion
        try {
            java.lang.reflect.Field f = View.class.getDeclaredField("detailPanel");
            f.setAccessible(true);
            String text = Reflect.areaText(f.get(view));
            assertTrue(text.contains("Estado: En marcha"), text);
            assertTrue(text.contains("Recurso A: stock 1"), text);
            assertTrue(text.contains("Productor 1 (fijo, Recurso A)"), text);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
