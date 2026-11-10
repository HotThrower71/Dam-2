package test.support;

import model.Resource;

// Hilo de prueba: produce o consume 1 unidad muchas veces seguidas (para probar la concurrencia)
public class RepeatTask implements Runnable {
    private final Resource resource;
    private final boolean produce;
    private final int times;

    public RepeatTask(Resource resource, boolean produce, int times) {
        this.resource = resource;
        this.produce = produce;
        this.times = times;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < times; i++) {
                if (produce) resource.produce(1); else resource.consume(1);
            }
        } catch (InterruptedException e) {
            // fin del hilo
        }
    }
}
