package test.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dto.Settings;
import test.support.SettingsBuilder;

class SettingsTest {

    @Test
    void dosAjustesIgualesTienenLaMismaEstructura() {
        assertTrue(new SettingsBuilder().build().sameStructure(new SettingsBuilder().build()));
    }

    @Test
    void tiemposUnidadesYLimitesNoCambianLaEstructura() {
        Settings base = new SettingsBuilder().build();

        SettingsBuilder b = new SettingsBuilder();
        b.producerDelayMin = 1;
        b.producerDelayMax = 99;
        b.consumerDelayMin = 2;
        b.consumerDelayMax = 98;
        b.producerUnits = 7;
        b.consumerUnits = 8;
        b.stockControl = false;
        b.useWaitNotify = false;
        b.synchronizedOps = false;
        b.maxStock = 40;
        b.minStock = 10;

        assertTrue(base.sameStructure(b.build()));
    }

    @Test
    void cadaCampoEstructuralCambiaLaEstructura() {
        Settings base = new SettingsBuilder().build();

        SettingsBuilder b = new SettingsBuilder();
        b.types = 2;
        assertFalse(base.sameStructure(b.build()), "tipos de recurso");

        b = new SettingsBuilder();
        b.fixedProducers = 2;
        assertFalse(base.sameStructure(b.build()), "productores fijos");

        b = new SettingsBuilder();
        b.variableProducers = 1;
        assertFalse(base.sameStructure(b.build()), "productores variables");

        b = new SettingsBuilder();
        b.fixedConsumers = 2;
        assertFalse(base.sameStructure(b.build()), "consumidores fijos");

        b = new SettingsBuilder();
        b.variableConsumers = 1;
        assertFalse(base.sameStructure(b.build()), "consumidores variables");

        b = new SettingsBuilder();
        b.producerUnlimited = false;
        assertFalse(base.sameStructure(b.build()), "productores con cuota");

        b = new SettingsBuilder();
        b.producerQuotaMax = 9;
        assertFalse(base.sameStructure(b.build()), "cuota maxima de productores");

        b = new SettingsBuilder();
        b.consumerUnlimited = false;
        assertFalse(base.sameStructure(b.build()), "consumidores con cuota");

        b = new SettingsBuilder();
        b.consumerQuotaMin = 3;
        assertFalse(base.sameStructure(b.build()), "cuota minima de consumidores");
    }

    @Test
    void esUnRecordConIgualdadPorValor() {
        assertEquals(new SettingsBuilder().build(), new SettingsBuilder().build());
    }
}
