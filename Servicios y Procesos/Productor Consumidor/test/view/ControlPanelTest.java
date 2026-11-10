package test.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import test.support.FakePresenter;
import test.support.Reflect;
import view.ControlPanel;

// Cada boton del ControlPanel tiene que llamar a su accion en el presenter
class ControlPanelTest {
    private ControlPanel panel;
    private FakePresenter presenter;

    @BeforeEach
    void setUp() {
        panel = new ControlPanel();
        presenter = new FakePresenter();
        panel.setPresenter(presenter);
    }

    @Test
    void iniciarLlamaAOnStart() {
        Reflect.button(panel, "startButton").doClick();

        assertEquals(1, presenter.starts);
        assertEquals(0, presenter.pauses);
        assertEquals(0, presenter.resets);
    }

    @Test
    void pausaLlamaAOnPause() {
        Reflect.button(panel, "pauseButton").doClick();

        assertEquals(1, presenter.pauses);
        assertEquals(0, presenter.starts);
    }

    @Test
    void reiniciarLlamaAOnReset() {
        Reflect.button(panel, "resetButton").doClick();

        assertEquals(1, presenter.resets);
        assertEquals(0, presenter.starts);
    }

    @Test
    void cadaClicEsUnaLlamada() {
        Reflect.button(panel, "startButton").doClick();
        Reflect.button(panel, "startButton").doClick();
        Reflect.button(panel, "startButton").doClick();

        assertEquals(3, presenter.starts);
    }
}
