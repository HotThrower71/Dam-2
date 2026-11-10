package test.support;

import java.util.ArrayList;
import java.util.List;

import dto.Settings;
import presenter.IPresenter;

// Presenter falso: apunta lo que le piden y devuelve los datos que se le configuren
public class FakePresenter implements IPresenter {
    public int starts, pauses, resets, applies, refreshes;
    public Settings lastSettings;

    public boolean running = false;
    public List<String> resourcesInfo = new ArrayList<>();
    public List<String> threadsInfo = new ArrayList<>();

    @Override public void start() { }
    @Override public void onStart() { starts++; }
    @Override public void onPause() { pauses++; }
    @Override public void onReset() { resets++; }

    @Override
    public void onApplySettings(Settings settings) {
        applies++;
        lastSettings = settings;
    }

    @Override public boolean isRunning() { refreshes++; return running; }
    @Override public int getProducerCount() { return 0; }
    @Override public int getConsumerCount() { return 0; }
    @Override public int getTotalProduced() { return 0; }
    @Override public int getTotalConsumed() { return 0; }
    @Override public int getWaitingProducers() { return 0; }
    @Override public int getWaitingConsumers() { return 0; }
    @Override public List<String> getResourcesInfo() { return resourcesInfo; }
    @Override public List<String> getThreadsInfo() { return threadsInfo; }
}
