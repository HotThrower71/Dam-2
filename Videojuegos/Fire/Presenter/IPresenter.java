package Presenter;

import java.util.List;

import Model.ColorTargetDTO;

/** Lo que la vista puede "contarle" al presentador (eventos del usuario). */
public interface IPresenter {
    void onPlayClicked();
    void onStopClicked();
    void onSparkPercentageChanged(double percentage);
    void onCoolPointsChanged(double percentage);
    void onPaletteChanged(List<ColorTargetDTO> targets);
}
