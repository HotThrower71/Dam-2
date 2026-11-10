package presenter;

import java.util.List;

import dto.Settings;
import model.IModel;
import view.IView;

// Intermediario: modelo y vista no se conocen entre si, los dos hablan con el Presenter
public class Presenter implements IPresenter {
    private final IModel model;
    private final IView view;

    public Presenter(IModel model, IView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void start() {
        view.setPresenter(this);
        view.start();
    }

    @Override
    public void onStart() { model.start(); }

    @Override
    public void onPause() { model.pause(); }

    @Override
    public void onReset() { model.reset(); }

    @Override
    public void onApplySettings(Settings settings) { model.applySettings(settings); }

    @Override
    public boolean isRunning() { return model.isRunning(); }

    @Override
    public int getProducerCount() { return model.getProducerCount(); }

    @Override
    public int getConsumerCount() { return model.getConsumerCount(); }

    @Override
    public int getTotalProduced() { return model.getTotalProduced(); }

    @Override
    public int getTotalConsumed() { return model.getTotalConsumed(); }

    @Override
    public int getWaitingProducers() { return model.getWaitingProducers(); }

    @Override
    public int getWaitingConsumers() { return model.getWaitingConsumers(); }

    @Override
    public List<String> getResourcesInfo() { return model.getResourcesInfo(); }

    @Override
    public List<String> getThreadsInfo() { return model.getThreadsInfo(); }
}
