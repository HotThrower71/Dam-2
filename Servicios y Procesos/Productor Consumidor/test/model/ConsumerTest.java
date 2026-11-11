package test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.Consumer;
import model.Resource;
import test.support.Threads;

class ConsumerTest {

    // n recursos sin limites y con "stock" unidades cada uno
    private List<Resource> resources(int n, int stock) throws InterruptedException {
        List<Resource> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Resource r = new Resource("Recurso " + (char) ('A' + i));
            r.configure(0, 0, true, true, true);
            if (stock > 0) r.produce(stock);
            list.add(r);
        }
        return list;
    }

    private int typesUsed(Consumer c, List<Resource> resources) {
        int used = 0;
        for (Resource r : resources) if (c.getConsumedFrom(r) > 0) used++;
        return used;
    }

    private int sumPerType(Consumer c, List<Resource> resources) {
        int sum = 0;
        for (Resource r : resources) sum += c.getConsumedFrom(r);
        return sum;
    }

    @Test
    void laCuotaLimitaElTotalConsumidoYElHiloTermina() throws Exception {
        List<Resource> list = resources(1, 10);
        Consumer c = new Consumer(list, false, "Consumidor 1", 1, 1, 2, 5, false);
        Thread t = Threads.start(c);

        assertTrue(Threads.waitFinished(c));
        assertTrue(Threads.waitFinished(t));
        assertEquals(5, c.getConsumed(), "con lotes de 2 y cuota 5 el ultimo lote es de 1");
        assertEquals(5, list.get(0).getStock());
        assertEquals(5, c.getQuota());
    }

    @Test
    void sinCuotaConsumeHastaQueLoInterrumpen() throws Exception {
        List<Resource> list = resources(1, 100);
        Consumer c = new Consumer(list, false, "Consumidor 1", 1, 1, 1, 0, false);
        Thread t = Threads.start(c);
        assertTrue(Threads.waitConsumed(c, 3));

        t.interrupt();

        assertTrue(Threads.waitFinished(t));
        assertFalse(c.isFinished());
    }

    @Test
    void pausadoNoConsumeHastaQueSeReanuda() throws Exception {
        List<Resource> list = resources(1, 100);
        Consumer c = new Consumer(list, false, "Consumidor 1", 1, 1, 1, 0, true);
        Thread t = Threads.start(c);

        Threads.sleep(150);
        assertEquals(0, c.getConsumed());

        c.setPaused(false);

        assertTrue(Threads.waitConsumed(c, 2));
        t.interrupt();
    }

    @Test
    void esperaSinStockYContinuaCuandoLlegaProducto() throws Exception {
        List<Resource> list = resources(1, 0);
        Consumer c = new Consumer(list, false, "Consumidor 1", 1, 1, 1, 0, false);
        Thread t = Threads.start(c);

        assertTrue(Threads.waitWaiting(c), "sin stock deberia estar esperando");
        assertEquals(0, c.getConsumed());

        list.get(0).produce(1);

        assertTrue(Threads.waitConsumed(c, 1));
        t.interrupt();
    }

    @Test
    void elFijoSiempreTrabajaConElMismoTipo() throws Exception {
        List<Resource> list = resources(3, 1000);
        Consumer c = new Consumer(list, false, "Consumidor 1", 1, 1, 1, 0, false);
        Thread t = Threads.start(c);
        assertTrue(Threads.waitConsumed(c, 30));
        t.interrupt();
        Threads.waitFinished(t);

        assertEquals(1, typesUsed(c, list));
        assertEquals(c.getConsumed(), sumPerType(c, list));
        assertTrue(c.getKind().startsWith("fijo"), c.getKind());
    }

    @Test
    void elVariableCambiaDeTipoEnCadaVuelta() throws Exception {
        List<Resource> list = resources(3, 1000);
        Consumer c = new Consumer(list, true, "Consumidor 1", 1, 1, 1, 0, false);
        Thread t = Threads.start(c);
        assertTrue(Threads.waitConsumed(c, 90));
        t.interrupt();
        Threads.waitFinished(t);

        assertTrue(typesUsed(c, list) >= 2);
        assertEquals(c.getConsumed(), sumPerType(c, list));
        assertTrue(c.getKind().startsWith("variable"), c.getKind());
    }

    @Test
    void cambiarLasUnidadesEnCalienteSeNota() throws Exception {
        List<Resource> list = resources(1, 1000);
        Consumer c = new Consumer(list, false, "Consumidor 1", 100, 100, 1, 0, true);
        c.configure(100, 100, 4);
        Thread t = Threads.start(c);
        c.setPaused(false);

        assertTrue(Threads.waitConsumed(c, 8));
        t.interrupt();
        Threads.waitFinished(t);

        assertEquals(0, c.getConsumed() % 4);
    }

    @Test
    void tieneNombre() throws Exception {
        Consumer c = new Consumer(resources(1, 0), false, "Consumidor 3", 1, 1, 1, 0, true);

        assertEquals("Consumidor 3", c.getName());
    }
}
