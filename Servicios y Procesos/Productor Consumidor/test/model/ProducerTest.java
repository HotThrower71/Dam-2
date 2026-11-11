package test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.Producer;
import model.Resource;
import test.support.Threads;

class ProducerTest {

    // n recursos sin limites
    private List<Resource> resources(int n) {
        List<Resource> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Resource r = new Resource("Recurso " + (char) ('A' + i));
            r.configure(0, 0, true, true, true);
            list.add(r);
        }
        return list;
    }

    private int typesUsed(Producer p, List<Resource> resources) {
        int used = 0;
        for (Resource r : resources) if (p.getProducedFrom(r) > 0) used++;
        return used;
    }

    private int sumPerType(Producer p, List<Resource> resources) {
        int sum = 0;
        for (Resource r : resources) sum += p.getProducedFrom(r);
        return sum;
    }

    @Test
    void laCuotaLimitaElTotalProducidoYElHiloTermina() {
        List<Resource> list = resources(1);
        Producer p = new Producer(list, false, "Productor 1", 1, 1, 2, 5, false);
        Thread t = Threads.start(p);

        assertTrue(Threads.waitFinished(p), "deberia marcarse como terminado");
        assertTrue(Threads.waitFinished(t), "el hilo deberia acabar");
        assertEquals(5, p.getProduced(), "con lotes de 2 y cuota 5 el ultimo lote es de 1");
        assertEquals(5, list.get(0).getStock());
        assertEquals(5, p.getQuota());
    }

    @Test
    void sinCuotaProduceHastaQueLoInterrumpen() {
        List<Resource> list = resources(1);
        Producer p = new Producer(list, false, "Productor 1", 1, 1, 1, 0, false);
        Thread t = Threads.start(p);
        assertTrue(Threads.waitProduced(p, 3));

        t.interrupt();

        assertTrue(Threads.waitFinished(t));
        assertFalse(p.isFinished(), "interrumpido no es lo mismo que terminado por cuota");
    }

    @Test
    void pausadoNoProduceHastaQueSeReanuda() {
        List<Resource> list = resources(1);
        Producer p = new Producer(list, false, "Productor 1", 1, 1, 1, 0, true);
        Thread t = Threads.start(p);

        Threads.sleep(150);
        assertEquals(0, p.getProduced(), "estaba en pausa");

        p.setPaused(false);

        assertTrue(Threads.waitProduced(p, 2));
        t.interrupt();
    }

    @Test
    void elFijoSiempreTrabajaConElMismoTipo() {
        List<Resource> list = resources(3);
        Producer p = new Producer(list, false, "Productor 1", 1, 1, 1, 0, false);
        Thread t = Threads.start(p);
        assertTrue(Threads.waitProduced(p, 30));
        t.interrupt();
        Threads.waitFinished(t);

        assertEquals(1, typesUsed(p, list), "un fijo usa un unico tipo");
        assertEquals(p.getProduced(), sumPerType(p, list));
        assertTrue(p.getKind().startsWith("fijo"), p.getKind());
    }

    @Test
    void elVariableCambiaDeTipoEnCadaVuelta() {
        List<Resource> list = resources(3);
        Producer p = new Producer(list, true, "Productor 1", 1, 1, 1, 0, false);
        Thread t = Threads.start(p);
        assertTrue(Threads.waitProduced(p, 90));
        t.interrupt();
        Threads.waitFinished(t);

        assertTrue(typesUsed(p, list) >= 2, "en 90 vueltas tiene que haber usado varios tipos");
        assertEquals(p.getProduced(), sumPerType(p, list));
        assertTrue(p.getKind().startsWith("variable"), p.getKind());
    }

    @Test
    void cambiarLasUnidadesEnCalienteSeNota() {
        List<Resource> list = resources(1);
        Producer p = new Producer(list, false, "Productor 1", 100, 100, 1, 0, true);
        p.configure(100, 100, 4); // ahora produce de 4 en 4
        Thread t = Threads.start(p);
        p.setPaused(false);

        assertTrue(Threads.waitProduced(p, 8));
        t.interrupt();
        Threads.waitFinished(t);

        assertEquals(0, p.getProduced() % 4, "cada vuelta suma 4");
    }

    @Test
    void esperaCuandoElRecursoEstaLleno() {
        List<Resource> list = resources(1);
        list.get(0).configure(2, 0, true, true, true);
        Producer p = new Producer(list, false, "Productor 1", 1, 1, 1, 0, false);
        Thread t = Threads.start(p);

        assertTrue(Threads.waitWaiting(p), "con el almacen lleno deberia estar esperando");
        assertEquals(2, list.get(0).getStock());
        assertEquals(2, p.getProduced());
        t.interrupt();
    }

    @Test
    void tieneNombre() {
        Producer p = new Producer(resources(1), false, "Productor 7", 1, 1, 1, 0, true);

        assertEquals("Productor 7", p.getName());
    }
}
