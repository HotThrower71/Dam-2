package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import dto.Settings;

public class Model implements IModel {
    // Ajustes iniciales, los mismos que los valores por defecto de la vista
    private Settings settings = new Settings(1, 1, 0, 1, 0, 200, 200, 200, 200, 1, 1,
            true, 0, 0, true, 0, 0, true, true, true, 0, 0);

    private final List<Resource> resources = new CopyOnWriteArrayList<>();
    private final List<Producer> producers = new CopyOnWriteArrayList<>();
    private final List<Consumer> consumers = new CopyOnWriteArrayList<>();
    private final List<Thread> threads = new CopyOnWriteArrayList<>();

    private volatile boolean running = false;

    public Model() {
        build();
    }

    // Para los hilos que hubiera y crea de cero recursos, productores y consumidores segun los ajustes
    private void build() {
        for (Thread t : threads) t.interrupt();
        threads.clear();
        producers.clear();
        consumers.clear();
        resources.clear();

        for (int i = 0; i < settings.resourceTypes(); i++) {
            resources.add(new Resource("Recurso " + (char) ('A' + i)));
        }
        configureResources();

        List<Resource> types = List.copyOf(resources); // los hilos guardan esta lista fija
        for (int i = 0; i < settings.fixedProducers(); i++) addProducer(types, false);
        for (int i = 0; i < settings.variableProducers(); i++) addProducer(types, true);
        for (int i = 0; i < settings.fixedConsumers(); i++) addConsumer(types, false);
        for (int i = 0; i < settings.variableConsumers(); i++) addConsumer(types, true);
    }

    private void configureResources() {
        for (Resource r : resources) {
            r.configure(settings.maxStock(), settings.minStock(), settings.stockControl(),
                    settings.useWaitNotify(), settings.synchronizedOps());
        }
    }

    private void addProducer(List<Resource> types, boolean variable) {
        int quota = settings.producerUnlimited() ? 0 : random(settings.producerQuotaMin(), settings.producerQuotaMax());
        Producer producer = new Producer(types, variable, "Productor " + (producers.size() + 1),
                settings.producerDelayMin(), settings.producerDelayMax(), settings.producerUnits(), quota, !running);
        producers.add(producer);
        launch(producer, producer.getName());
    }

    private void addConsumer(List<Resource> types, boolean variable) {
        int quota = settings.consumerUnlimited() ? 0 : random(settings.consumerQuotaMin(), settings.consumerQuotaMax());
        Consumer consumer = new Consumer(types, variable, "Consumidor " + (consumers.size() + 1),
                settings.consumerDelayMin(), settings.consumerDelayMax(), settings.consumerUnits(), quota, !running);
        consumers.add(consumer);
        launch(consumer, consumer.getName());
    }

    private int random(int min, int max) {
        return max <= min ? min : ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private void launch(Runnable runnable, String name) {
        Thread thread = new Thread(runnable, name);
        thread.setDaemon(true);
        threads.add(thread);
        thread.start();
    }

    // Si cambia la estructura (cuantos hilos, de que tipo, cuantos recursos, cuotas) se vuelve a crear todo.
    // Tiempos, unidades y limites se aplican en caliente a los hilos que ya existen
    @Override
    public void applySettings(Settings s) {
        boolean rebuild = !settings.sameStructure(s);
        settings = s;
        if (rebuild) {
            build();
        } else {
            configureResources();
            for (Producer p : producers) p.configure(s.producerDelayMin(), s.producerDelayMax(), s.producerUnits());
            for (Consumer c : consumers) c.configure(s.consumerDelayMin(), s.consumerDelayMax(), s.consumerUnits());
        }
    }

    @Override
    public void start() {
        setPaused(false);
    }

    @Override
    public void pause() {
        setPaused(true);
    }

    private void setPaused(boolean paused) {
        running = !paused;
        for (Producer p : producers) p.setPaused(paused);
        for (Consumer c : consumers) c.setPaused(paused);
    }

    // Deja la simulacion a cero con los ajustes actuales (y vuelve a sortear los tipos de los hilos fijos)
    @Override
    public void reset() {
        running = false;
        build();
    }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public int getProducerCount() { return producers.size(); }

    @Override
    public int getConsumerCount() { return consumers.size(); }

    @Override
    public int getTotalProduced() {
        int total = 0;
        for (Producer p : producers) total += p.getProduced();
        return total;
    }

    @Override
    public int getTotalConsumed() {
        int total = 0;
        for (Consumer c : consumers) total += c.getConsumed();
        return total;
    }

    @Override
    public int getWaitingProducers() {
        int total = 0;
        for (Producer p : producers) if (p.isWaiting()) total++;
        return total;
    }

    @Override
    public int getWaitingConsumers() {
        int total = 0;
        for (Consumer c : consumers) if (c.isWaiting()) total++;
        return total;
    }

    // Una linea por tipo de recurso. El descuadre es lo que producido - consumido - stock deberia ser 0:
    // si no lo es, se han perdido actualizaciones por no sincronizar
    @Override
    public List<String> getResourcesInfo() {
        List<String> info = new ArrayList<>();
        for (Resource r : resources) {
            int produced = 0, consumed = 0;
            for (Producer p : producers) produced += p.getProducedFrom(r);
            for (Consumer c : consumers) consumed += c.getConsumedFrom(r);
            String limits = r.isStockControl()
                    ? "máx " + (r.getCapacity() == 0 ? "sin límite" : r.getCapacity()) + ", mín " + r.getMinimum()
                    : "SIN CONTROL";
            info.add(r.getName() + ": stock " + r.getStock() + "  (" + limits + ")"
                    + "  producido: " + produced + "  consumido: " + consumed
                    + "  descuadre: " + (produced - consumed - r.getStock()));
        }
        return info;
    }

    @Override
    public List<String> getThreadsInfo() {
        List<String> info = new ArrayList<>();
        for (Producer p : producers) {
            info.add(p.getName() + " (" + p.getKind() + ")  ->  producidos: " + p.getProduced()
                    + (p.getQuota() > 0 ? " / " + p.getQuota() : "")
                    + (p.isFinished() ? "   [TERMINADO]" : p.isWaiting() ? "   [ESPERANDO: stock lleno]" : ""));
        }
        for (Consumer c : consumers) {
            info.add(c.getName() + " (" + c.getKind() + ")  ->  consumidos: " + c.getConsumed()
                    + (c.getQuota() > 0 ? " / " + c.getQuota() : "")
                    + (c.isFinished() ? "   [TERMINADO]" : c.isWaiting() ? "   [ESPERANDO: no hay stock]" : ""));
        }
        return info;
    }
}
