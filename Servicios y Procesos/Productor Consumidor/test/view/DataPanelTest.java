package test.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dto.Settings;
import test.support.FakePresenter;
import test.support.Reflect;
import view.DataPanel;

// El DataPanel valida lo escrito y, solo si todo es correcto, manda los Settings al presenter
class DataPanelTest {
    private DataPanel panel;
    private FakePresenter presenter;

    @BeforeEach
    void setUp() {
        panel = new DataPanel();
        presenter = new FakePresenter();
        panel.setPresenter(presenter); // aplica los valores por defecto: applies == 1
    }

    // Escribe y pulsa Aplicar
    private void apply(String field, String text) {
        Reflect.setText(panel, field, text);
        panel.apply();
    }

    private void assertRejected(String expectedMessage) {
        assertEquals(1, presenter.applies, "no debe llegar nada al presenter");
        assertTrue(Reflect.message(panel).contains(expectedMessage), Reflect.message(panel));
    }

    // ---------- Valores por defecto ----------

    @Test
    void alConectarElPresenterMandaLosValoresPorDefecto() {
        assertEquals(1, presenter.applies);
        assertEquals(new Settings(1, 1, 0, 1, 0, 200, 200, 200, 200, 1, 1,
                true, 0, 0, true, 0, 0, true, true, true, 0, 0), presenter.lastSettings);
        assertEquals("Ajustes aplicados", Reflect.message(panel));
    }

    @Test
    void unValorCorrectoLlegaAlPresenter() {
        apply("producerDelayMin", "50");
        apply("producerDelayMax", "80");

        assertEquals(3, presenter.applies);
        assertEquals(50, presenter.lastSettings.producerDelayMin());
        assertEquals(80, presenter.lastSettings.producerDelayMax());
    }

    @Test
    void losEspaciosAlrededorSeIgnoran() {
        apply("fixedProducers", "  4  ");

        assertEquals(4, presenter.lastSettings.fixedProducers());
    }

    // ---------- Numeros invalidos ----------

    @Test
    void textoQueNoEsUnNumeroSeRechaza() {
        apply("producerDelayMin", "abc");

        assertRejected("no es un número entero");
    }

    @Test
    void campoVacioSeRechaza() {
        apply("consumerUnits", "");

        assertRejected("no es un número entero");
    }

    @Test
    void decimalesSeRechazan() {
        apply("producerUnits", "1.5");

        assertRejected("no es un número entero");
    }

    @Test
    void elTiempoMinimoDebeSerAlMenosUno() {
        apply("producerDelayMin", "0");

        assertRejected("mínimo 1");
    }

    @Test
    void elTiempoMaximoNoPuedeSerMenorQueElMinimo() {
        Reflect.setText(panel, "producerDelayMin", "50");
        apply("producerDelayMax", "10");

        assertRejected("Tiempo producir máx: mínimo 50");
    }

    @Test
    void lasUnidadesPorConsumicionDebenSerAlMenosUna() {
        apply("consumerUnits", "0");

        assertRejected("Unidades por consumición");
    }

    @Test
    void elNumeroDeHilosNoPuedeSerNegativo() {
        apply("fixedConsumers", "-1");

        assertRejected("mínimo 0");
    }

    @Test
    void nuncaMasDeCienHilos() {
        apply("variableProducers", "101");

        assertRejected("máximo 100");
    }

    @Test
    void sePuedeTenerCeroHilos() {
        Reflect.setText(panel, "fixedProducers", "0");
        apply("fixedConsumers", "0");

        assertEquals(2, presenter.applies);
        assertEquals(0, presenter.lastSettings.fixedProducers());
    }

    @Test
    void nuncaMasDeVeintiseisTiposDeRecurso() {
        apply("resourceTypes", "27");

        assertRejected("máximo 26");
    }

    @Test
    void veintiseisTiposSonValidos() {
        apply("resourceTypes", "26");

        assertEquals(26, presenter.lastSettings.resourceTypes());
    }

    @Test
    void almenosUnTipoDeRecurso() {
        apply("resourceTypes", "0");

        assertRejected("mínimo 1");
    }

    // ---------- Cuotas ----------

    @Test
    void siSonIlimitadosSeIgnoranLosRangosDeCuota() {
        Reflect.setText(panel, "producerQuotaMin", "abc");
        Reflect.setText(panel, "consumerQuotaMax", "-9");
        panel.apply();

        assertEquals(2, presenter.applies, "con Ilimitados marcado los campos de cuota no se leen");
        assertTrue(presenter.lastSettings.producerUnlimited());
        assertEquals(0, presenter.lastSettings.producerQuotaMin());
        assertEquals(0, presenter.lastSettings.consumerQuotaMax());
    }

    @Test
    void sinIlimitadosSeLeeElRangoDeCuota() {
        Reflect.setSelected(panel, "producerUnlimited", false);
        Reflect.setText(panel, "producerQuotaMin", "3");
        Reflect.setText(panel, "producerQuotaMax", "7");
        panel.apply();

        assertFalse(presenter.lastSettings.producerUnlimited());
        assertEquals(3, presenter.lastSettings.producerQuotaMin());
        assertEquals(7, presenter.lastSettings.producerQuotaMax());
    }

    @Test
    void laCuotaMaximaNoPuedeSerMenorQueLaMinima() {
        Reflect.setSelected(panel, "consumerUnlimited", false);
        Reflect.setText(panel, "consumerQuotaMin", "9");
        apply("consumerQuotaMax", "2");

        assertRejected("Recursos a consumir máx");
    }

    @Test
    void sinIlimitadosLaCuotaDebeSerAlMenosUno() {
        Reflect.setSelected(panel, "producerUnlimited", false);
        apply("producerQuotaMin", "0");

        assertRejected("Recursos a producir mín");
    }

    // ---------- Reglas entre ajustes ----------

    @Test
    void minimoMasUnidadesPorConsumicionNoPuedeSuperarElMaximo() {
        Reflect.setText(panel, "maxStock", "3");
        Reflect.setText(panel, "minStock", "3");
        apply("consumerUnits", "1"); // 3 + 1 > 3: el consumidor esperaria para siempre

        assertRejected("no puede superar el máximo");
    }

    @Test
    void minimoMasUnidadesIgualAlMaximoEsValido() {
        Reflect.setText(panel, "maxStock", "4");
        Reflect.setText(panel, "minStock", "3");
        apply("consumerUnits", "1");

        assertEquals(2, presenter.applies);
    }

    @Test
    void conMaximoCeroNoHayRegla() {
        Reflect.setText(panel, "maxStock", "0");
        apply("minStock", "50");

        assertEquals(2, presenter.applies, "0 = sin limite, el minimo puede ser cualquiera");
    }

    @Test
    void sinControlDeStockNoSeValidanLosLimites() {
        Reflect.setSelected(panel, "stockControl", false);
        Reflect.setText(panel, "maxStock", "3");
        apply("minStock", "3");

        assertEquals(2, presenter.applies);
        assertFalse(presenter.lastSettings.stockControl());
    }

    @Test
    void sinSincronizacionNoSePuedeUsarWaitNotify() {
        Reflect.setSelected(panel, "synchronizedOps", false); // y el desplegable sigue en wait/notifyAll
        panel.apply();

        assertRejected("Sin sincronización");
    }

    @Test
    void sinSincronizacionConSleepSeAplica() {
        Reflect.setSelected(panel, "synchronizedOps", false);
        Reflect.selectItem(panel, "controlType", "sleep");
        panel.apply();

        assertEquals(2, presenter.applies);
        assertFalse(presenter.lastSettings.synchronizedOps());
        assertFalse(presenter.lastSettings.useWaitNotify());
    }

    @Test
    void sinControlDeStockYSinSincronizacionNoHaceFaltaSleep() {
        Reflect.setSelected(panel, "stockControl", false);
        Reflect.setSelected(panel, "synchronizedOps", false);
        panel.apply();

        assertEquals(2, presenter.applies, "sin control no se espera nunca, da igual el tipo");
    }

    @Test
    void despuesDeUnErrorUnaCorreccionSeAplica() {
        apply("producerDelayMin", "abc");
        assertEquals(1, presenter.applies);

        apply("producerDelayMin", "25");

        assertEquals(2, presenter.applies);
        assertEquals("Ajustes aplicados", Reflect.message(panel));
    }
}
