package test.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.event.ActionEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import test.support.FakePresenter;
import view.DataPanel;
import view.listeners.ApplyListener;
import view.listeners.PauseListener;
import view.listeners.ResetListener;
import view.listeners.StartListener;

// Cada listener hace una sola cosa: llamar a lo suyo
class ListenersTest {
    private FakePresenter presenter;
    private ActionEvent event;

    @BeforeEach
    void setUp() {
        presenter = new FakePresenter();
        event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "click");
    }

    @Test
    void startListenerLlamaAOnStart() {
        new StartListener(presenter).actionPerformed(event);

        assertEquals(1, presenter.starts);
        assertEquals(0, presenter.pauses + presenter.resets);
    }

    @Test
    void pauseListenerLlamaAOnPause() {
        new PauseListener(presenter).actionPerformed(event);

        assertEquals(1, presenter.pauses);
        assertEquals(0, presenter.starts + presenter.resets);
    }

    @Test
    void resetListenerLlamaAOnReset() {
        new ResetListener(presenter).actionPerformed(event);

        assertEquals(1, presenter.resets);
        assertEquals(0, presenter.starts + presenter.pauses);
    }

    @Test
    void applyListenerHaceQueElDataPanelAplique() {
        DataPanel panel = new DataPanel();
        panel.setPresenter(presenter); // 1 aplicacion al conectar

        new ApplyListener(panel).actionPerformed(event);

        assertEquals(2, presenter.applies);
    }
}
