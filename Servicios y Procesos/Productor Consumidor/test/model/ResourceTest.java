package test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import model.Resource;
import test.support.ConsumeTask;
import test.support.ProduceTask;
import test.support.RepeatTask;
import test.support.Threads;

// Las pruebas con "waitNotify" se repiten dos veces: true = wait()/notifyAll(), false = sleep
class ResourceTest {

    private Resource resource(int capacity, int minimum, boolean control, boolean waitNotify, boolean sync) {
        Resource r = new Resource("Recurso A");
        r.configure(capacity, minimum, control, waitNotify, sync);
        return r;
    }

    // ---------- Lo basico ----------

    @Test
    void producirSumaAlStockYDevuelveLasUnidades() throws Exception {
        Resource r = resource(0, 0, true, true, true);

        assertEquals(3, r.produce(3));

        assertEquals(3, r.getStock());
    }

    @Test
    void consumirRestaDelStockYDevuelveLasUnidades() throws Exception {
        Resource r = resource(0, 0, true, true, true);
        r.produce(5);

        assertEquals(2, r.consume(2));

        assertEquals(3, r.getStock());
    }

    @Test
    void guardaSuNombreYSuConfiguracion() {
        Resource r = resource(7, 2, true, false, true);

        assertEquals("Recurso A", r.getName());
        assertEquals(7, r.getCapacity());
        assertEquals(2, r.getMinimum());
        assertTrue(r.isStockControl());
        assertEquals(0, r.getStock());
    }

    @Test
    void unHiloNuloNuncaEstaBloqueado() {
        assertFalse(resource(0, 0, true, true, true).isBlocked(null));
    }

    // ---------- Esperas ----------

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void elConsumidorEsperaSinStockYSeDespiertaAlProducir(boolean waitNotify) throws Exception {
        Resource r = resource(0, 0, true, waitNotify, true);
        ConsumeTask task = new ConsumeTask(r, 1);
        Thread t = Threads.start(task);

        assertTrue(Threads.waitBlocked(r, t), "el consumidor deberia quedarse esperando");
        assertFalse(task.done);

        r.produce(1);

        assertTrue(Threads.waitFinished(t), "el consumidor deberia despertar");
        assertEquals(1, task.result);
        assertEquals(0, r.getStock());
        assertFalse(r.isBlocked(t), "ya no esta bloqueado");
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void elProductorEsperaEnElMaximoYSeDespiertaAlConsumir(boolean waitNotify) throws Exception {
        Resource r = resource(3, 0, true, waitNotify, true);
        r.produce(3);
        ProduceTask task = new ProduceTask(r, 1);
        Thread t = Threads.start(task);

        assertTrue(Threads.waitBlocked(r, t), "el productor deberia esperar con el almacen lleno");
        assertEquals(3, r.getStock());

        r.consume(1);

        assertTrue(Threads.waitFinished(t));
        assertEquals(1, task.result);
        assertEquals(3, r.getStock());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void elConsumidorNoBajaDelMinimo(boolean waitNotify) throws Exception {
        Resource r = resource(0, 2, true, waitNotify, true);
        r.produce(2);
        ConsumeTask task = new ConsumeTask(r, 1);
        Thread t = Threads.start(task);

        assertTrue(Threads.waitBlocked(r, t), "con stock = minimo no puede consumir");
        assertEquals(2, r.getStock());

        r.produce(1);

        assertTrue(Threads.waitFinished(t));
        assertEquals(2, r.getStock(), "debe quedar el minimo");
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void subirElMaximoDespiertaAlProductorBloqueado(boolean waitNotify) throws Exception {
        Resource r = resource(2, 0, true, waitNotify, true);
        r.produce(2);
        ProduceTask task = new ProduceTask(r, 1);
        Thread t = Threads.start(task);
        assertTrue(Threads.waitBlocked(r, t));

        r.configure(0, 0, true, waitNotify, true); // sin limite

        assertTrue(Threads.waitFinished(t), "al cambiar el limite hay que despertar a los hilos dormidos");
        assertEquals(3, r.getStock());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void bajarElMinimoDespiertaAlConsumidorBloqueado(boolean waitNotify) throws Exception {
        Resource r = resource(0, 2, true, waitNotify, true);
        r.produce(2);
        ConsumeTask task = new ConsumeTask(r, 1);
        Thread t = Threads.start(task);
        assertTrue(Threads.waitBlocked(r, t));

        r.configure(0, 0, true, waitNotify, true);

        assertTrue(Threads.waitFinished(t));
        assertEquals(1, r.getStock());
    }

    @Test
    void notifyAllDespiertaATodosLosHilosDormidos() throws Exception {
        Resource r = resource(0, 0, true, true, true);
        ConsumeTask[] tasks = new ConsumeTask[4];
        Thread[] threads = new Thread[4];
        for (int i = 0; i < 4; i++) {
            tasks[i] = new ConsumeTask(r, 1);
            threads[i] = Threads.start(tasks[i]);
        }
        for (Thread t : threads) assertTrue(Threads.waitBlocked(r, t));

        r.produce(4); // un solo produce con 4 unidades: tienen que salir los 4

        for (Thread t : threads) assertTrue(Threads.waitFinished(t), "no puede quedar ningun hilo dormido para siempre");
        assertEquals(0, r.getStock());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void interrumpirUnHiloBloqueadoLoLibera(boolean waitNotify) throws Exception {
        Resource r = resource(0, 0, true, waitNotify, true);
        ConsumeTask task = new ConsumeTask(r, 1);
        Thread t = Threads.start(task);
        assertTrue(Threads.waitBlocked(r, t));

        t.interrupt();

        assertTrue(Threads.waitFinished(t));
        assertNotNull(task.error, "debe terminar con InterruptedException");
        assertFalse(task.done);
        assertFalse(r.isBlocked(t), "al salir deja de contar como bloqueado");
    }

    // ---------- Lotes ----------

    @Test
    void unLoteMayorQueElMaximoSeRecortaEnVezDeBloquearseParaSiempre() throws Exception {
        Resource r = resource(3, 0, true, true, true);

        assertEquals(3, r.produce(10));

        assertEquals(3, r.getStock());
    }

    @Test
    void unLoteDeConsumoSeRecortaAlHuecoUtil() throws Exception {
        Resource r = resource(4, 1, true, true, true);
        r.produce(4);

        assertEquals(3, r.consume(10), "como maximo capacidad - minimo");
        assertEquals(1, r.getStock());
    }

    // ---------- Sin control de stock ----------

    @Test
    void sinControlElConsumidorNoEsperaYElStockPuedeSerNegativo() throws Exception {
        Resource r = resource(0, 0, false, true, true);

        assertEquals(2, r.consume(2));

        assertEquals(-2, r.getStock());
    }

    @Test
    void sinControlElProductorIgnoraElMaximo() throws Exception {
        Resource r = resource(1, 0, false, true, true);

        assertEquals(5, r.produce(5));

        assertEquals(5, r.getStock());
    }

    // ---------- Concurrencia ----------

    @Test
    void conSincronizacionNoSePierdeNingunaActualizacion() throws Exception {
        // Sin control para que ningun hilo espere: solo importa que las sumas y restas sean exactas
        Resource r = resource(0, 0, false, true, true);
        Thread[] threads = new Thread[8];
        for (int i = 0; i < 8; i++) {
            threads[i] = Threads.start(new RepeatTask(r, i < 4, 2000));
        }
        for (Thread t : threads) assertTrue(Threads.waitFinished(t));

        assertEquals(0, r.getStock(), "4 hilos x 2000 producidos y 4 x 2000 consumidos tienen que cuadrar");
    }

    @Test
    void conControlYWaitNotifyProductoresYConsumidoresTerminanSinBloquearseParaSiempre() throws Exception {
        Resource r = resource(5, 0, true, true, true);
        Thread[] threads = new Thread[6];
        for (int i = 0; i < 6; i++) {
            threads[i] = Threads.start(new RepeatTask(r, i < 3, 1000));
        }
        for (Thread t : threads) assertTrue(Threads.waitFinished(t), "hilo dormido para siempre");

        assertEquals(0, r.getStock());
    }

    @Test
    void conControlYSleepProductoresYConsumidoresTambienTerminan() throws Exception {
        Resource r = resource(5, 0, true, false, true);
        Thread[] threads = new Thread[6];
        for (int i = 0; i < 6; i++) {
            threads[i] = Threads.start(new RepeatTask(r, i < 3, 200));
        }
        for (Thread t : threads) assertTrue(Threads.waitFinished(t), "hilo dormido para siempre");

        assertEquals(0, r.getStock());
    }

    @Test
    void conMaximoElStockNuncaLoSupera() throws Exception {
        Resource r = resource(5, 0, true, true, true);
        Thread[] producers = new Thread[4];
        for (int i = 0; i < 4; i++) producers[i] = Threads.start(new RepeatTask(r, true, 500));

        int highest = 0;
        for (int i = 0; i < 200; i++) {
            highest = Math.max(highest, r.getStock());
            Threads.sleep(1);
        }

        assertTrue(highest <= 5, "el stock llego a " + highest);
        // Hay 4 productores y nadie consume: acaban bloqueados con el almacen lleno
        assertEquals(5, r.getStock());
        for (Thread t : producers) t.interrupt();
    }
}
