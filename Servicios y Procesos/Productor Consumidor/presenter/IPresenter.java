package presenter;

import java.util.List;

import dto.Settings;

public interface IPresenter {
    void start();

    // Acciones del usuario (ControlPanel)
    void onStart();
    void onPause();
    void onReset();

    // Ajustes de la simulacion (DataPanel)
    void onApplySettings(Settings settings);

    // Datos para pintar (DetailPanel)
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
