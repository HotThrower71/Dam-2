import java.awt.Color;
import java.util.List;

import javax.swing.SwingUtilities;

import Model.ColorTargetDTO;
import Model.Model;
import Presenter.Presenter;
import View.View;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Model model = new Model();
            View view = new View();
            Presenter presenter = new Presenter(model, view);

            model.setDimension(200, 200);
            presenter.onSparkPercentageChanged(view.getSparkValue());
            presenter.onCoolPointsChanged(view.getCoolPointsValue());

            // Paleta: negro -> rojo -> amarillo -> blanco
            presenter.onPaletteChanged(List.of(
                    new ColorTargetDTO(0, Color.BLACK),
                    new ColorTargetDTO(100, Color.RED),
                    new ColorTargetDTO(200, Color.YELLOW),
                    new ColorTargetDTO(255, Color.WHITE)));

            view.setVisible(true);
        });
    }
}
