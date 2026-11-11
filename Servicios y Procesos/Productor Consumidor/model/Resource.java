package model;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Un tipo de recurso con su propio stock, limites y forma de esperar
public class Resource {
    private static final int POLL_MS = 20; // cada cuanto vuelve a mirar un hilo en modo sleep

    private final String name;
    private volatile int stock; // volatile: se lee desde la vista; la sincronizacion de verdad la pone synchronized
    private int capacity;  // maximo de stock, 0 = sin limite
    private int minimum;   // stock que tiene que quedar siempre
    private volatile boolean stockControl = true;
    private volatile boolean useWaitNotify = true;
    private volatile boolean synchronizedOps = true;

    // Hilos que ahora mismo no pueden avanzar (para mostrarlo en pantalla)
    private final Set<Thread> blocked = ConcurrentHashMap.newKeySet();

    public Resource(String name){
        this.name = name;
    }

    public int produce(int units) throws InterruptedException {
        return operate(units, true);
    }

    public int consume(int units) throws InterruptedException {
        return operate(units, false);
    }

    // Produce o consume. Devuelve las unidades movidas
    private int operate(int units, boolean produce) throws InterruptedException {
        try {
            while (true) {
                int n = produce ? limitProduce(units) : limitConsume(units);
                if (synchronizedOps) {
                    synchronized (this) {
                        if (can(produce, n)) {
                            stock += produce ? n : -n;
                            notifyAll(); // despierta a todos para que vuelvan a comprobar su condicion
                            return n;
                        }
                        if (useWaitNotify) {
                            blocked.add(Thread.currentThread());
                            wait(); // suelta el candado y duerme hasta el proximo notifyAll
                            continue;
                        }
                    }
                } else {
                    // Sin candado: comprobar y cambiar el stock son dos pasos separados
                    if (can(produce, n)) {
                        unsafeAdd(produce ? n : -n);
                        return n;
                    }
                }
                // Modo sleep: se duerme FUERA del candado, si no nadie mas podria entrar a cambiar el stock
                blocked.add(Thread.currentThread());
                Thread.sleep(POLL_MS);
            }
        } finally {
            blocked.remove(Thread.currentThread());
        }
    }

    // Lectura y escritura por separado, con un yield en medio, para que se vea la condicion de carrera
    private void unsafeAdd(int delta){
        int tmp = stock;
        Thread.yield();
        stock = tmp + delta;
    }

    private boolean can(boolean produce, int n){
        if (!stockControl) return true;
        return produce ? (capacity == 0 || stock + n <= capacity) : (stock - n >= minimum);
    }

    // Un lote nunca puede ser mayor que el hueco posible, si no el hilo esperaria para siempre
    private int limitProduce(int units){
        return stockControl && capacity > 0 ? Math.min(units, capacity) : units;
    }

    private int limitConsume(int units){
        return stockControl && capacity > 0 ? Math.max(1, Math.min(units, capacity - minimum)) : units;
    }

    // Al cambiar los ajustes se despierta a todos para que vuelvan a comprobar su condicion
    public synchronized void configure(int capacity, int minimum, boolean stockControl, boolean useWaitNotify,
            boolean synchronizedOps){
        this.capacity = capacity;
        this.minimum = minimum;
        this.stockControl = stockControl;
        this.useWaitNotify = useWaitNotify;
        this.synchronizedOps = synchronizedOps;
        notifyAll();
    }

    public boolean isBlocked(Thread thread){ return thread != null && blocked.contains(thread); }
    public int getBlockedCount(){ return blocked.size(); }
    public String getName(){ return name; }
    public int getStock(){ return stock; }
    public int getCapacity(){ return capacity; }
    public int getMinimum(){ return minimum; }
    public boolean isStockControl(){ return stockControl; }
}
//Mirarse los listeners para usar los hilos del entorno grafico (Swing)
// Diferencia entr ecrear hilo con runnable y con thread


//Aumentar numeros de productores y consumidores ,poner sycnronized y no en probar jugar con los tiempos cantidades etz
