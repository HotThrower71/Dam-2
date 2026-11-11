package test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Model;
import test.support.SettingsBuilder;
import test.support.Threads;

class ModelTest {
    private Model model;
    private SettingsBuilder b;

    @BeforeEach
    void setUp() {
        model = new Model();
        b = new SettingsBuilder();
    }

    // Deja la simulacion sin hilos para que no sigan corriendo al acabar la prueba
    @AfterEach
    void tearDown() {
        model.applySettings(SettingsBuilder.empty());
    }

    // ---------- Estado inicial y estructura ----------

    @Test
    void alInicioHayUnProductorUnConsumidorYUnRecursoParados() {
        assertEquals(1, model.getProducerCount());
        assertEquals(1, model.getConsumerCount());
        assertFalse(model.isRunning());
        assertEquals(1, model.getResourcesInfo().size());
        assertEquals(2, model.getThreadsInfo().size());
        assertEquals(0, model.getTotalProduced());
        assertEquals(0, model.getTotalConsumed());
    }

    @Test
    void aplicarUnaEstructuraNuevaCreaLosHilosPedidos() {
        b.types = 3;
        b.fixedProducers = 2;
        b.variableProducers = 1;
        b.fixedConsumers = 1;
        b.variableConsumers = 2;

        model.applySettings(b.build());

        assertEquals(3, model.getProducerCount());
        assertEquals(3, model.getConsumerCount());
        assertEquals(3, model.getResourcesInfo().size());
        assertEquals(6, model.getThreadsInfo().size());
    }

    @Test
    void losRecursosSeLlamanA_B_C() {
        b.types = 3;
        model.applySettings(b.build());

        List<String> info = model.getResourcesInfo();

        assertTrue(info.get(0).startsWith("Recurso A:"), info.get(0));
        assertTrue(info.get(1).startsWith("Recurso B:"), info.get(1));
        assertTrue(info.get(2).startsWith("Recurso C:"), info.get(2));
    }

    @Test
    void losHilosFijosVanPrimeroYLuegoLosVariables() {
        b.fixedProducers = 2;
        b.variableProducers = 1;
        model.applySettings(b.build());

        List<String> info = model.getThreadsInfo();

        assertTrue(info.get(0).startsWith("Productor 1 (fijo"), info.get(0));
        assertTrue(info.get(1).startsWith("Productor 2 (fijo"), info.get(1));
        assertTrue(info.get(2).startsWith("Productor 3 (variable"), info.get(2));
        assertTrue(info.get(3).startsWith("Consumidor 1"), info.get(3));
    }

    @Test
    void sinHilosNoHayNada() {
        model.applySettings(SettingsBuilder.empty());

        assertEquals(0, model.getProducerCount());
        assertEquals(0, model.getConsumerCount());
        assertTrue(model.getThreadsInfo().isEmpty());
    }

    // ---------- Aplicar ajustes: estructura o en caliente ----------

    @Test
    void cambiarLaEstructuraVuelveACrearLosHilosYPonePuestosLosContadoresACero() {
        model.start();
        assertTrue(Threads.waitTotalProduced(model, 5));
        model.pause();
        Threads.sleep(100);
        assertTrue(model.getTotalProduced() > 0);

        b.fixedProducers = 2; // cambia la estructura
        model.applySettings(b.build());

        assertEquals(2, model.getProducerCount());
        assertEquals(0, model.getTotalProduced(), "los hilos nuevos empiezan de cero");
    }

    @Test
    void cambiarSoloTiemposYLimitesConservaLosContadores() {
        model.start();
        assertTrue(Threads.waitTotalProduced(model, 5));
        model.pause();
        Threads.sleep(100);
        int produced = model.getTotalProduced();

        b.producerDelayMin = 1;
        b.producerDelayMax = 9;
        b.producerUnits = 2;
        b.maxStock = 50;
        b.minStock = 1;
        model.applySettings(b.build());

        assertEquals(1, model.getProducerCount());
        assertEquals(produced, model.getTotalProduced(), "no se reconstruye: los hilos siguen siendo los mismos");
    }

    @Test
    void cambiarLaEstructuraConservaSiEstabaEnMarchaOParada() {
        model.start();
        b.fixedProducers = 2;
        model.applySettings(b.build());
        assertTrue(model.isRunning());

        model.pause();
        b.fixedProducers = 3;
        model.applySettings(b.build());
        assertFalse(model.isRunning());
    }

    // ---------- Iniciar, pausar, reiniciar ----------

    @Test
    void startYPauseCambianElEstado() {
        model.start();
        assertTrue(model.isRunning());

        model.pause();
        assertFalse(model.isRunning());
    }

    @Test
    void sinIniciarNoSeProduceNada() {
        Threads.sleep(150);

        assertEquals(0, model.getTotalProduced());
    }

    @Test
    void iniciadoSeProduceYSeConsume() {
        model.start();

        assertTrue(Threads.waitTotalProduced(model, 5));
        assertTrue(Threads.waitTotalConsumed(model, 3));
    }

    @Test
    void pauseDetieneLaProduccion() {
        model.start();
        assertTrue(Threads.waitTotalProduced(model, 3));

        model.pause();
        Threads.sleep(100); // deja terminar la vuelta en curso
        int produced = model.getTotalProduced();
        Threads.sleep(200);

        assertEquals(produced, model.getTotalProduced());
    }

    @Test
    void resetDejaTodoACeroYParadoConLosMismosHilos() {
        model.start();
        assertTrue(Threads.waitTotalProduced(model, 5));

        model.reset();

        assertFalse(model.isRunning());
        assertEquals(0, model.getTotalProduced());
        assertEquals(0, model.getTotalConsumed());
        assertEquals(1, model.getProducerCount());
        assertEquals(1, model.getConsumerCount());
        Threads.sleep(100);
        assertEquals(0, model.getTotalProduced(), "tras reiniciar sigue parado");
    }

    // ---------- Limites de stock ----------

    @Test
    void conMaximoElStockNoLoSuperaYLosProductoresEsperan() {
        b.fixedProducers = 3;
        b.fixedConsumers = 0;
        b.maxStock = 5;
        model.applySettings(b.build());
        model.start();

        assertTrue(Threads.waitWaitingProducers(model, 3), "los 3 productores deberian acabar esperando");
        assertEquals(5, model.getTotalProduced(), "no puede haberse producido mas que el maximo");
        assertTrue(model.getResourcesInfo().get(0).contains("stock 5"), model.getResourcesInfo().get(0));
        assertTrue(Threads.waitThreadInfo(model, "[ESPERANDO: stock lleno]"));
    }

    @Test
    void conMinimoElConsumidorNuncaDejaElStockPorDebajo() {
        b.minStock = 3;
        b.producerDelayMin = 30;
        b.producerDelayMax = 30;
        b.consumerDelayMin = 1;
        b.consumerDelayMax = 1;
        model.applySettings(b.build());
        model.start();

        assertTrue(Threads.waitTotalConsumed(model, 3));
        model.pause();
        Threads.sleep(100);

        int stock = model.getTotalProduced() - model.getTotalConsumed(); // un solo recurso
        assertTrue(stock >= 3, "el stock era " + stock);
    }

    @Test
    void sinProductoresElConsumidorSeQuedaEsperando() {
        b.fixedProducers = 0;
        model.applySettings(b.build());
        model.start();

        assertTrue(Threads.waitWaitingConsumers(model, 1));
        assertTrue(Threads.waitThreadInfo(model, "[ESPERANDO: no hay stock]"));
        assertEquals(0, model.getTotalConsumed());
    }

    @Test
    void sinControlElStockPuedeQuedarNegativo() {
        b.fixedProducers = 0;
        b.stockControl = false;
        model.applySettings(b.build());
        model.start();

        assertTrue(Threads.waitTotalConsumed(model, 3));
        model.pause();
        String line = model.getResourcesInfo().get(0);

        assertTrue(line.contains("SIN CONTROL"), line);
        assertTrue(line.contains("stock -"), line);
    }

    // ---------- Cuotas ----------

    @Test
    void unProductorConCuotaTerminaYSeMarcaComoTerminado() {
        b.fixedConsumers = 0;
        b.producerUnlimited = false;
        b.producerQuotaMin = 3;
        b.producerQuotaMax = 3;
        model.applySettings(b.build());
        model.start();

        assertTrue(Threads.waitThreadInfo(model, "[TERMINADO]"));
        assertEquals(3, model.getTotalProduced());
        assertTrue(model.getThreadsInfo().get(0).contains("/ 3"), model.getThreadsInfo().get(0));
    }

    @Test
    void siSeAcabanLosProductoresElConsumidorIlimitadoSeQuedaEsperando() {
        b.producerUnlimited = false;
        b.producerQuotaMin = 4;
        b.producerQuotaMax = 4;
        model.applySettings(b.build());
        model.start();

        // Primero el productor llega a su cuota, luego el consumidor consume todo y se queda sin nada
        assertTrue(Threads.waitThreadInfo(model, "[TERMINADO]"));
        assertTrue(Threads.waitTotalConsumed(model, 4));
        assertTrue(Threads.waitWaitingConsumers(model, 1));
        assertEquals(4, model.getTotalProduced());
        assertEquals(4, model.getTotalConsumed());
    }

    // ---------- Modos de control y consistencia ----------

    @Test
    void conSleepTambienSeProduceYSeConsume() {
        b.useWaitNotify = false;
        model.applySettings(b.build());
        model.start();

        assertTrue(Threads.waitTotalConsumed(model, 5));
    }

    @Test
    void conSincronizacionElDescuadreEsCeroEnCadaRecurso() {
        b.types = 2;
        b.fixedProducers = 2;
        b.variableProducers = 2;
        b.fixedConsumers = 2;
        b.variableConsumers = 2;
        b.producerDelayMin = 1;
        b.producerDelayMax = 3;
        model.applySettings(b.build());
        model.start();
        assertTrue(Threads.waitTotalProduced(model, 200));
        model.pause();
        Threads.sleep(150); // que terminen las operaciones en curso

        for (String line : model.getResourcesInfo()) {
            assertTrue(line.endsWith("descuadre: 0"), line);
        }
    }

    @Test
    void conVariosTiposLosFijosSoloTocanSuRecurso() {
        b.types = 3;
        b.fixedProducers = 3;
        b.fixedConsumers = 0;
        model.applySettings(b.build());
        model.start();
        assertTrue(Threads.waitTotalProduced(model, 30));
        model.pause();

        // Cada productor fijo aparece con un unico tipo en su descripcion
        for (String line : model.getThreadsInfo()) {
            assertTrue(line.contains("(fijo, Recurso "), line);
        }
    }
}
