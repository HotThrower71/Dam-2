package view.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import presenter.IPresenter;

// Se ejecuta al pulsar "Reiniciar"
public class ResetListener implements ActionListener {
    private final IPresenter presenter;

    public ResetListener(IPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.onReset();
    }
}
