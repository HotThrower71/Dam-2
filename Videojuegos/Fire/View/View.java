package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import Presenter.IPresenter;

public class View extends JFrame implements IView {
    private IPresenter presenter;

    private JLabel fireLabel = new JLabel();
    private JButton playButton = new JButton("Play");
    private JButton stopButton = new JButton("Stop");
    private JSlider sparksSlider = new JSlider(0, 100, 70);
    private JSlider coolPointsSlider = new JSlider(0, 100, 20);

    public View() {
        super("Fire");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        fireLabel.setOpaque(true);
        fireLabel.setBackground(Color.BLACK);

        JPanel controls = new JPanel();
        controls.add(playButton);
        controls.add(stopButton);
        controls.add(new JLabel("Chispas"));
        controls.add(sparksSlider);
        controls.add(new JLabel("Puntos frios"));
        controls.add(coolPointsSlider);

        add(fireLabel, BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);

        playButton.addActionListener(e -> presenter.onPlayClicked());
        stopButton.addActionListener(e -> presenter.onStopClicked());
        sparksSlider.addChangeListener(e -> presenter.onSparkPercentageChanged(sparksSlider.getValue() / 100.0));
        coolPointsSlider.addChangeListener(e -> presenter.onCoolPointsChanged(coolPointsSlider.getValue() / 100.0));

        setSize(820, 520);
    }

    public double getSparkValue() {
        return sparksSlider.getValue() / 100.0;
    }

    public double getCoolPointsValue() {
        return coolPointsSlider.getValue() / 100.0;
    }

    @Override
    public void updateImage(BufferedImage image) {
        if (image == null || fireLabel.getWidth() == 0)
            return;
        fireLabel.setIcon(new ImageIcon(image.getScaledInstance(fireLabel.getWidth(), fireLabel.getHeight(), Image.SCALE_FAST)));
    }

    @Override
    public void setPresenter(IPresenter presenter) {
        this.presenter = presenter;
    }
}
