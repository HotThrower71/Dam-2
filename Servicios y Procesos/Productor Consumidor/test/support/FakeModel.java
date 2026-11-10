package test.support;

import java.util.ArrayList;
import java.util.List;

import dto.Settings;
import model.IModel;

// Modelo falso: apunta las llamadas que recibe y devuelve unos datos fijos
public class FakeModel implements IModel {
    public final List<String> calls = new ArrayList<>();
    public Settings lastSettings;

    public boolean running = true;
    public int producerCount = 3;
    public int consumerCount = 2;
    public int totalProduced = 40;
    public int totalConsumed = 35;
    public int waitingProducers = 1;
    public int waitingConsumers = 2;
    public final List<String> resourcesInfo = new ArrayList<>();
    public final List<String> threadsInfo = new ArrayList<>();

    @Override
    public void applySettings(Settings settings) {
        calls.add("applySettings");
        lastSettings = settings;
    }

    @Override public void start() { calls.add("start"); }
    @Override public void pause() { calls.add("pause"); }
    @Override public void reset() { calls.add("reset"); }
    @Override public boolean isRunning() { return running; }
    @Override public int getProducerCount() { return producerCount; }
    @Override public int getConsumerCount() { return consumerCount; }
    @Override public int getTotalProduced() { return totalProduced; }
    @Override public int getTotalConsumed() { return totalConsumed; }
    @Override public int getWaitingProducers() { return waitingProducers; }
    @Override public int getWaitingConsumers() { return waitingConsumers; }
    @Override public List<String> getResourcesInfo() { return resourcesInfo; }
    @Override public List<String> getThreadsInfo() { return threadsInfo; }
}
