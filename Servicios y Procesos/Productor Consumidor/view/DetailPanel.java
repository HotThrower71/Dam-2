package view;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// Zona grande: resumen global de la simulacion y datos de cada productor y consumidor
public class DetailPanel extends JPanel {
    private final JTextArea text = new JTextArea();

    public DetailPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Datos"));
        text.setEditable(false);
        add(new JScrollPane(text), BorderLayout.CENTER);
    }

    public void update(boolean running, int producers, int consumers, int waitingProducers, int waitingConsumers,
            int produced, int consumed, List<String> resources, List<String> info) {
        StringBuilder sb = new StringBuilder();
        sb.append("Estado: ").append(running ? "En marcha" : "Parada").append('\n');
        sb.append("Productores esperando: ").append(waitingProducers).append(" de ").append(producers).append('\n');
        sb.append("Consumidores esperando: ").append(waitingConsumers).append(" de ").append(consumers).append('\n');
        sb.append("Total producido: ").append(produced).append('\n');
        sb.append("Total consumido: ").append(consumed).append("\n\n");
        sb.append(String.join("\n", resources)).append("\n\n");
        sb.append(String.join("\n", info));
        text.setText(sb.toString());
    }
}
