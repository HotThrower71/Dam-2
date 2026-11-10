package view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.Timer;

import presenter.IPresenter;
import view.listeners.RefreshListener;

// La ventana: columna izquierda (1/3) con ControlPanel arriba y DataPanel (ajustes) abajo, resto (2/3) con el resumen y los datos de los hilos
public class View implements IView {
    private IPresenter presenter;

    private final JFrame frame;
    private final ControlPanel controlPanel;
    private final DataPanel dataPanel;
    private final DetailPanel detailPanel;

    private final int REFRESH_MS = 100;

    public View() {
        controlPanel = new ControlPanel();
        dataPanel = new DataPanel();
        detailPanel = new DetailPanel();

        // Tamaños preferidos 1 : 2 para que la columna izquierda sea 1/3
        controlPanel.setPreferredSize(new Dimension(520, 120));
        dataPanel.setPreferredSize(new Dimension(520, 580));
        detailPanel.setPreferredSize(new Dimension(1040, 700));

        frame = new JFrame("Productor - Consumidor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        // Arriba izquierda: control panel
        c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
        frame.add(controlPanel, c);

        // Abajo izquierda: data panel
        c.gridx = 0; c.gridy = 1; c.weightx = 1; c.weighty = 1;
        frame.add(dataPanel, c);

        // Derecha: ocupa las dos filas y el doble de ancho
        c.gridx = 1; c.gridy = 0; c.gridheight = 2; c.weightx = 2; c.weighty = 1;
        frame.add(detailPanel, c);
    }

    @Override
    public void setPresenter(IPresenter presenter) {
        this.presenter = presenter;
        controlPanel.setPresenter(presenter);
        dataPanel.setPresenter(presenter);
    }

    @Override
    public void start() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Timer de Swing: refresca los datos en el hilo del entorno grafico
        new Timer(REFRESH_MS, new RefreshListener(this)).start();
    }

    // Lo llama RefreshListener
    public void refresh() {
        detailPanel.update(presenter.isRunning(), presenter.getProducerCount(), presenter.getConsumerCount(),
                presenter.getWaitingProducers(), presenter.getWaitingConsumers(),
                presenter.getTotalProduced(), presenter.getTotalConsumed(),
                presenter.getResourcesInfo(), presenter.getThreadsInfo());
    }
}
