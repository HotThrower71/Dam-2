package view.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import presenter.IPresenter;

// Se ejecuta al pulsar "Iniciar"
public class StartListener implements ActionListener {
    private final IPresenter presenter;

    public StartListener(IPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.onStart();
    }
}
