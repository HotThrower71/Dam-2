package test.support;

import model.Resource;

// Hilo de prueba: hace una sola produccion y guarda el resultado
public class ProduceTask implements Runnable {
    private final Resource resource;
    private final int units;

    public volatile int result = -1;
    public volatile boolean done = false;
    public volatile InterruptedException error;

    public ProduceTask(Resource resource, int units) {
        this.resource = resource;
        this.units = units;
    }

    @Override
    public void run() {
        try {
            result = resource.produce(units);
            done = true;
        } catch (InterruptedException e) {
            error = e;
        }
    }
}
