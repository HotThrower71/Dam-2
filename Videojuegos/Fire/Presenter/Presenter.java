package Presenter;

import java.util.List;

import javax.swing.Timer;

import Model.ColorTargetDTO;
import Model.IModel;
import View.IView;

public class Presenter implements IPresenter {
    private IModel model;
    private IView view;
    private Timer refreshTimer; // refresca la vista mientras el modelo corre

    public Presenter(IModel model, IView view) {
        this.model = model;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void onPlayClicked() {
        model.play();
        if (refreshTimer == null)
            refreshTimer = new Timer(30, e -> view.updateImage(model.getImage()));
        refreshTimer.start();
    }

    @Override
    public void onStopClicked() {
        model.stop();
        if (refreshTimer != null)
            refreshTimer.stop();
    }

    @Override
    public void onSparkPercentageChanged(double percentage) {
        model.setSparks(percentage);
    }

    @Override
    public void onCoolPointsChanged(double percentage) {
        model.setCoolPoints(percentage);
    }

    @Override
    public void onPaletteChanged(List<ColorTargetDTO> targets) {
        model.setPalette(targets);
    }
}
