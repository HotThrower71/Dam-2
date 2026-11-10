package test.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dto.Settings;
import presenter.Presenter;
import test.support.FakeModel;
import test.support.FakeView;
import test.support.SettingsBuilder;

// El Presenter solo delega: se comprueba con un modelo y una vista falsos
class PresenterTest {
    private FakeModel model;
    private FakeView view;
    private Presenter presenter;

    @BeforeEach
    void setUp() {
        model = new FakeModel();
        view = new FakeView();
        presenter = new Presenter(model, view);
    }

    @Test
    void startLeDaElPresenterALaVistaYLaArranca() {
        presenter.start();

        assertSame(presenter, view.presenter);
        assertTrue(view.started);
    }

    @Test
    void lasAccionesDelUsuarioLlegaranAlModelo() {
        presenter.onStart();
        presenter.onPause();
        presenter.onReset();

        assertEquals(3, model.calls.size());
        assertEquals("start", model.calls.get(0));
        assertEquals("pause", model.calls.get(1));
        assertEquals("reset", model.calls.get(2));
    }

    @Test
    void aplicarAjustesPasaElMismoObjetoAlModelo() {
        Settings settings = new SettingsBuilder().build();

        presenter.onApplySettings(settings);

        assertEquals("applySettings", model.calls.get(0));
        assertSame(settings, model.lastSettings);
    }

    @Test
    void losDatosParaPintarSalenDelModelo() {
        model.resourcesInfo.add("Recurso A: stock 4");
        model.threadsInfo.add("Productor 1");

        assertTrue(presenter.isRunning());
        assertEquals(3, presenter.getProducerCount());
        assertEquals(2, presenter.getConsumerCount());
        assertEquals(40, presenter.getTotalProduced());
        assertEquals(35, presenter.getTotalConsumed());
        assertEquals(1, presenter.getWaitingProducers());
        assertEquals(2, presenter.getWaitingConsumers());
        assertSame(model.resourcesInfo, presenter.getResourcesInfo());
        assertSame(model.threadsInfo, presenter.getThreadsInfo());
    }

    @Test
    void siElModeloCambiaElPresenterDevuelveLoNuevo() {
        model.running = false;
        model.totalProduced = 99;

        assertFalse(presenter.isRunning());
        assertEquals(99, presenter.getTotalProduced());
    }
}
