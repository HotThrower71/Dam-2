package model;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Consumer implements Runnable {
    private final List<Resource> resources;
    private final boolean variable; // true = elige tipo de recurso en cada vuelta; false = uno fijo
    private final String name;
    private final int quota; // total de unidades que consumira este hilo, 0 = ilimitado
    private final AtomicIntegerArray consumedPerType;
    private volatile Resource current; // recurso con el que trabaja ahora (el fijo, o el ultimo elegido)
    private volatile int delayMin;
    private volatile int delayMax;
    private volatile int units;
    private volatile boolean paused;
    private volatile int consumed;
    private volatile Thread thread;
    private volatile boolean finished;

    public Consumer(List<Resource> resources, boolean variable, String name, int delayMin, int delayMax, int units,
            int quota, boolean paused){
        this.resources = resources;
        this.variable = variable;
        this.name = name;
        this.delayMin = delayMin;
        this.delayMax = delayMax;
        this.units = units;
        this.quota = quota;
        this.paused = paused;
        this.consumedPerType = new AtomicIntegerArray(resources.size());
        this.current = randomResource(); // el fijo se queda con este para siempre
    }

    @Override
    public void run(){
        thread = Thread.currentThread();
        try {
            // El hilo se para con interrupt() (reinicio) o al llegar a su cuota
            while (!Thread.currentThread().isInterrupted() && (quota == 0 || consumed < quota)) {
                if (paused) {
                    Thread.sleep(50);
                    continue;
                }
                if (variable) current = randomResource();
                Resource resource = current;
                int batch = quota == 0 ? units : Math.min(units, quota - consumed);
                int taken = resource.consume(batch);
                consumedPerType.addAndGet(resources.indexOf(resource), taken);
                consumed += taken;
                Thread.sleep(randomDelay());
            }
            finished = quota > 0 && consumed >= quota;
        } catch (InterruptedException e) {
            // fin del hilo
        }
    }

    private Resource randomResource(){
        return resources.get(ThreadLocalRandom.current().nextInt(resources.size()));
    }

    // Tiempo de descanso aleatorio entre el minimo y el maximo
    private int randomDelay(){
        int min = delayMin, max = delayMax;
        return max <= min ? min : ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public void configure(int delayMin, int delayMax, int units){
        this.delayMin = delayMin;
        this.delayMax = delayMax;
        this.units = units;
    }
    public void setPaused(boolean paused){ this.paused = paused; }
    public int getConsumed(){ return consumed; }
    public int getConsumedFrom(Resource resource){ return consumedPerType.get(resources.indexOf(resource)); }
    public int getQuota(){ return quota; }
    public boolean isFinished(){ return finished; }
    public boolean isWaiting(){ return current.isBlocked(thread); }
    public String getKind(){ return variable ? "variable, ahora " + current.getName() : "fijo, " + current.getName(); }
    public String getName(){ return name; }
}
