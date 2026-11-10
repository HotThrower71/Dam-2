package test.support;

import model.Resource;

// Hilo de prueba: hace un solo consumo y guarda el resultado
public class ConsumeTask implements Runnable {
    private final Resource resource;
    private final int units;

    public volatile int result = -1;
    public volatile boolean done = false;
    public volatile InterruptedException error;

    public ConsumeTask(Resource resource, int units) {
        this.resource = resource;
        this.units = units;
    }

    @Override
    public void run() {
        try {
            result = resource.consume(units);
            done = true;
        } catch (InterruptedException e) {
            error = e;
        }
    }
}
