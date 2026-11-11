package model;

import java.util.List;

import dto.Settings;

public interface IModel {
    void applySettings(Settings settings);
    void start();
    void pause();
    void reset();

    boolean isRunning();
    int getProducerCount();
    int getConsumerCount();
    int getTotalProduced();
    int getTotalConsumed();
    int getWaitingProducers();
    int getWaitingConsumers();
    List<String> getResourcesInfo();
    List<String> getThreadsInfo();
}
