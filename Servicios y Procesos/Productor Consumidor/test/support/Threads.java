package test.support;

import model.Consumer;
import model.IModel;
import model.Producer;
import model.Resource;

// Ayudas para las pruebas con hilos: arrancar y esperar sin depender de tiempos fijos.
// Cada espera consulta la condicion cada 10 ms y se rinde a los 3 segundos
public final class Threads {
    private static final int TIMEOUT_MS = 3000;
    private static final int STEP_MS = 10;

    private Threads() {
    }

    public static Thread start(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // El hilo ha terminado por si solo
    public static boolean waitFinished(Thread thread) {
        try {
            thread.join(TIMEOUT_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return !thread.isAlive();
    }

    // El hilo esta esperando dentro del recurso
    public static boolean waitBlocked(Resource resource, Thread thread) {
        for (int waited = 0; waited < TIMEOUT_MS; waited += STEP_MS) {
            if (resource.isBlocked(thread)) return true;
            sleep(STEP_MS);
        }
        return false;
    }

    public static boolean waitProduced(Producer p, int n) {
        for (int waited = 0; waited < TIMEOUT_MS; waited += STEP_MS) {
            if (p.getProduced() >= n) return true;
            sleep(STEP_MS);
        }
        return false;
    }

    public static boolean waitFinished(Producer p) {
        for (int waited = 0; waited < TIMEOUT_MS; waited += STEP_MS) {
            if (p.isFinished()) return true;
            sleep(STEP_MS);
        }
        return false;
    }

    public static boolean waitWaiting(Producer p) {
        for (int waited = 0; waited < TIMEOUT_MS; waited += STEP_MS) {
            if (p.isWaiting()) return true;
            sleep(STEP_MS);
        }
        return false;
    }

    public static boolean waitConsumed(Consumer c, int n) {
        for (int waited = 0; waited < TIMEOUT_MS; waited += STEP_MS) {
            if (c.getConsumed() >= n) return true;
            sleep(STEP_MS);
        }
        return false;
    }

    public static boolean waitFinished(Consumer c) {
        for (int waited = 0; waited < TIMEOUT_MS; waited += STEP_MS) {
            if (c.isFinished()) return true;
            sleep(STEP_MS);
        }
        return false;
    }

    public static boolean waitWaiting(Consumer c) {
        for (int waited = 0; waited < TIMEOUT_MS; waited += STEP_MS) {
            if (c.isWaiting()) return true;
            sleep(STEP_MS);
        }
        return false;
    }

    public static boolean waitTotalProduced(IModel model, int n) {
        for (int waited = 0; waited < TIMEOUT_MS; waited += STEP_MS) {
            if (model.getTotalProduced() >= n) return true;
            sleep(STEP_MS);
        }
        return false;
    }

    public static boolean waitTotalConsumed(IModel model, int n) {
        for (int waited = 0; waited < TIMEOUT_MS; waited += STEP_MS) {
            if (model.getTotalConsumed() >= n) return true;
            sleep(STEP_MS);
        }
        return false;
    }

    public static boolean waitWaitingProducers(IModel model, int n) {
        for (int waited = 0; waited < TIMEOUT_MS; waited += STEP_MS) {
            if (model.getWaitingProducers() == n) return true;
            sleep(STEP_MS);
        }
        return false;
    }

    public static boolean waitWaitingConsumers(IModel model, int n) {
        for (int waited = 0; waited < TIMEOUT_MS; waited += STEP_MS) {
            if (model.getWaitingConsumers() == n) return true;
            sleep(STEP_MS);
        }
        return false;
    }

    // Alguna linea de informacion de hilos contiene el texto
    public static boolean waitThreadInfo(IModel model, String text) {
        for (int waited = 0; waited < TIMEOUT_MS; waited += STEP_MS) {
            for (String line : model.getThreadsInfo()) {
                if (line.contains(text)) return true;
            }
            sleep(STEP_MS);
        }
        return false;
    }
}
