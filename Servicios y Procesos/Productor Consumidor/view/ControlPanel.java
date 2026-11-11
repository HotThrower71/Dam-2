package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import presenter.IPresenter;
import view.listeners.PauseListener;
import view.listeners.ResetListener;
import view.listeners.StartListener;

// Panel de control: start / pause / reset. Cuantos hilos hay se decide en el DataPanel
public class ControlPanel extends JPanel {
    private final JButton startButton = new JButton("Iniciar");
    private final JButton pauseButton = new JButton("Pausa");
    private final JButton resetButton = new JButton("Reiniciar");

    public ControlPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Control Panel"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        c.gridy = 0;
        c.gridx = 0; add(startButton, c);
        c.gridx = 1; add(pauseButton, c);
        c.gridx = 2; add(resetButton, c);

        // Empuja todo hacia arriba
        c.gridx = 0; c.gridy = 1; c.gridwidth = 3; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
        add(new JPanel(), c);

    }

    // Los listeners necesitan al presenter, asi que se conectan cuando ya lo tenemos
    public void setPresenter(IPresenter presenter) {
        startButton.addActionListener(new StartListener(presenter));
        pauseButton.addActionListener(new PauseListener(presenter));
        resetButton.addActionListener(new ResetListener(presenter));
    }
}
