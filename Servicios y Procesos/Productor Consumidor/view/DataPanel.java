package view;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dto.Settings;
import presenter.IPresenter;
import view.listeners.ApplyListener;

// Panel de datos: campos para ajustar la simulacion
public class DataPanel extends JPanel {
    private static final String WAIT_NOTIFY = "wait / notifyAll";
    private static final String SLEEP = "sleep";

    private IPresenter presenter;

    private final JTextField resourceTypes = new JTextField("1", 4);
    private final JTextField fixedProducers = new JTextField("1", 4);
    private final JTextField variableProducers = new JTextField("0", 4);
    private final JTextField fixedConsumers = new JTextField("1", 4);
    private final JTextField variableConsumers = new JTextField("0", 4);
    private final JTextField producerDelayMin = new JTextField("200", 5);
    private final JTextField producerDelayMax = new JTextField("200", 5);
    private final JTextField consumerDelayMin = new JTextField("200", 5);
    private final JTextField consumerDelayMax = new JTextField("200", 5);
    private final JTextField producerUnits = new JTextField("1", 4);
    private final JTextField consumerUnits = new JTextField("1", 4);
    private final JCheckBox producerUnlimited = new JCheckBox("Ilimitados", true);
    private final JTextField producerQuotaMin = new JTextField("10", 4);
    private final JTextField producerQuotaMax = new JTextField("20", 4);
    private final JCheckBox consumerUnlimited = new JCheckBox("Ilimitados", true);
    private final JTextField consumerQuotaMin = new JTextField("10", 4);
    private final JTextField consumerQuotaMax = new JTextField("20", 4);
    private final JCheckBox stockControl = new JCheckBox("Control de stock", true);
    private final JComboBox<String> controlType = new JComboBox<>(new String[] { WAIT_NOTIFY, SLEEP });
    private final JCheckBox synchronizedOps = new JCheckBox("Sincronizar producir / consumir", true);
    private final JTextField maxStock = new JTextField("0", 4);
    private final JTextField minStock = new JTextField("0", 4);
    private final JButton applyButton = new JButton("Aplicar");
    private final JLabel message = new JLabel(" ");

    private int row = 0;

    public DataPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Data Panel"));

        addRow("Tipos de recurso:", resourceTypes);
        addRow("Nº productores:", new JLabel("fijos"), fixedProducers, new JLabel("variables"), variableProducers);
        addRow("Nº consumidores:", new JLabel("fijos"), fixedConsumers, new JLabel("variables"), variableConsumers);
        addRow("Tiempo producir (ms):", new JLabel("mín"), producerDelayMin, new JLabel("máx"), producerDelayMax);
        addRow("Tiempo consumir (ms):", new JLabel("mín"), consumerDelayMin, new JLabel("máx"), consumerDelayMax);
        addRow("Unidades por producción:", producerUnits);
        addRow("Unidades por consumición:", consumerUnits);
        addRow("Recursos a producir (por hilo):", producerUnlimited, new JLabel("mín"), producerQuotaMin,
                new JLabel("máx"), producerQuotaMax);
        addRow("Recursos a consumir (por hilo):", consumerUnlimited, new JLabel("mín"), consumerQuotaMin,
                new JLabel("máx"), consumerQuotaMax);
        addRow("", stockControl);
        addRow("Tipo de control de stock:", controlType);
        addRow("", synchronizedOps);
        addRow("Límite de stock:", new JLabel("mín"), minStock, new JLabel("máx (0 = sin límite)"), maxStock);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.gridx = 0; c.gridwidth = 2; c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = row++; add(applyButton, c);
        c.gridy = row++; add(message, c);

        // Empuja todo hacia arriba
        c.gridy = row; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
        add(new JPanel(), c);

        ApplyListener applyListener = new ApplyListener(this);
        applyButton.addActionListener(applyListener);
        // Enter en cualquier campo tambien aplica
        for (JTextField field : new JTextField[] { resourceTypes, fixedProducers, variableProducers,
                fixedConsumers, variableConsumers, producerDelayMin, producerDelayMax,
                consumerDelayMin, consumerDelayMax, producerUnits, consumerUnits, producerQuotaMin,
                producerQuotaMax, consumerQuotaMin, consumerQuotaMax, maxStock, minStock }) {
            field.addActionListener(applyListener);
        }
    }

    // Una fila: etiqueta a la izquierda y el resto de componentes en linea a la derecha
    private void addRow(String title, JComponent... components) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 6, 3, 6);
        c.anchor = GridBagConstraints.WEST;
        c.gridy = row++;
        c.gridx = 0; add(new JLabel(title), c);
        JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        for (JComponent component : components) line.add(component);
        c.gridx = 1; c.weightx = 1; add(line, c);
    }

    // Los valores iniciales de los campos se mandan al modelo al conectar el presenter
    public void setPresenter(IPresenter presenter) {
        this.presenter = presenter;
        apply();
    }

    // Lo llama ApplyListener
    public void apply() {
        try {
            int pDelayMin = read(producerDelayMin, 1, "Tiempo producir mín");
            int pDelayMax = read(producerDelayMax, pDelayMin, "Tiempo producir máx");
            int cDelayMin = read(consumerDelayMin, 1, "Tiempo consumir mín");
            int cDelayMax = read(consumerDelayMax, cDelayMin, "Tiempo consumir máx");
            int pUnits = read(producerUnits, 1, "Unidades por producción");
            int cUnits = read(consumerUnits, 1, "Unidades por consumición");

            // Los rangos de cuota solo importan si no son ilimitados
            int pQuotaMin = 0, pQuotaMax = 0, cQuotaMin = 0, cQuotaMax = 0;
            if (!producerUnlimited.isSelected()) {
                pQuotaMin = read(producerQuotaMin, 1, "Recursos a producir mín");
                pQuotaMax = read(producerQuotaMax, pQuotaMin, "Recursos a producir máx");
            }
            if (!consumerUnlimited.isSelected()) {
                cQuotaMin = read(consumerQuotaMin, 1, "Recursos a consumir mín");
                cQuotaMax = read(consumerQuotaMax, cQuotaMin, "Recursos a consumir máx");
            }

            int types = read(resourceTypes, 1, "Tipos de recurso");
            if (types > 26) throw new IllegalArgumentException("Tipos de recurso: máximo 26");
            int fixedP = readCount(fixedProducers, "Productores fijos");
            int variableP = readCount(variableProducers, "Productores variables");
            int fixedC = readCount(fixedConsumers, "Consumidores fijos");
            int variableC = readCount(variableConsumers, "Consumidores variables");
            int min = read(minStock, 0, "Stock mínimo");
            int max = read(maxStock, 0, "Stock máximo");
            boolean control = stockControl.isSelected();
            boolean waitNotify = WAIT_NOTIFY.equals(controlType.getSelectedItem());
            boolean sync = synchronizedOps.isSelected();

            // Si el mínimo + lo que saca un consumidor no cabe en el máximo, el consumidor esperaría para siempre
            if (control && max > 0 && min + cUnits > max) {
                throw new IllegalArgumentException("Mínimo + unidades por consumición no puede superar el máximo");
            }
            // wait() y notifyAll() exigen estar dentro de un synchronized
            if (control && !sync && waitNotify) {
                throw new IllegalArgumentException("Sin sincronización solo se puede usar el control con sleep");
            }

            presenter.onApplySettings(new Settings(types, fixedP, variableP, fixedC, variableC, pDelayMin, pDelayMax, cDelayMin, cDelayMax, pUnits, cUnits,
                    producerUnlimited.isSelected(), pQuotaMin, pQuotaMax,
                    consumerUnlimited.isSelected(), cQuotaMin, cQuotaMax,
                    control, waitNotify, sync, max, min));
            message.setText("Ajustes aplicados");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            message.setText(ex.getMessage());
        }
    }

    // Numero de hilos: de 0 a 100
    private int readCount(JTextField field, String name) {
        int value = read(field, 0, name);
        if (value > 100) throw new IllegalArgumentException(name + ": máximo 100");
        return value;
    }

    private int read(JTextField field, int min, String name) {
        try {
            int value = Integer.parseInt(field.getText().trim());
            if (value < min) throw new IllegalArgumentException(name + ": mínimo " + min);
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + ": no es un número entero");
        }
    }
}
