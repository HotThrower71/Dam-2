package test.support;

import dto.Settings;

// Construye unos Settings de prueba: los valores por defecto del programa, pero con descansos de 5 ms
// para que las pruebas vayan rapido. Se cambia el campo que interese y se llama a build()
public class SettingsBuilder {
    public int types = 1;
    public int fixedProducers = 1;
    public int variableProducers = 0;
    public int fixedConsumers = 1;
    public int variableConsumers = 0;
    public int producerDelayMin = 5;
    public int producerDelayMax = 5;
    public int consumerDelayMin = 5;
    public int consumerDelayMax = 5;
    public int producerUnits = 1;
    public int consumerUnits = 1;
    public boolean producerUnlimited = true;
    public int producerQuotaMin = 0;
    public int producerQuotaMax = 0;
    public boolean consumerUnlimited = true;
    public int consumerQuotaMin = 0;
    public int consumerQuotaMax = 0;
    public boolean stockControl = true;
    public boolean useWaitNotify = true;
    public boolean synchronizedOps = true;
    public int maxStock = 0;
    public int minStock = 0;

    public Settings build() {
        return new Settings(types, fixedProducers, variableProducers, fixedConsumers, variableConsumers,
                producerDelayMin, producerDelayMax, consumerDelayMin, consumerDelayMax,
                producerUnits, consumerUnits,
                producerUnlimited, producerQuotaMin, producerQuotaMax,
                consumerUnlimited, consumerQuotaMin, consumerQuotaMax,
                stockControl, useWaitNotify, synchronizedOps, maxStock, minStock);
    }

    // Sin ningun hilo: sirve para parar la simulacion al terminar una prueba
    public static Settings empty() {
        SettingsBuilder b = new SettingsBuilder();
        b.fixedProducers = 0;
        b.fixedConsumers = 0;
        return b.build();
    }
}
