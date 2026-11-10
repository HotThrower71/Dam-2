package view.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import view.DataPanel;

// Se ejecuta al pulsar "Aplicar" o al dar Enter en un campo del DataPanel
public class ApplyListener implements ActionListener {
    private final DataPanel dataPanel;

    public ApplyListener(DataPanel dataPanel) {
        this.dataPanel = dataPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dataPanel.apply();
    }
}
