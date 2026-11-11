package view.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import view.View;

// Lo dispara el Timer de Swing cada pocos milisegundos para refrescar la pantalla
public class RefreshListener implements ActionListener {
    private final View view;

    public RefreshListener(View view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        view.refresh();
    }
}
