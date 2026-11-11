package view.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import presenter.IPresenter;

// Se ejecuta al pulsar "Pausa"
public class PauseListener implements ActionListener {
    private final IPresenter presenter;

    public PauseListener(IPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.onPause();
    }
}
