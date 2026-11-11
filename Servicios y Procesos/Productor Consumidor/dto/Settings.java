package dto;

// Todos los ajustes de la simulacion juntos: la vista los rellena y el modelo los aplica
public record Settings(
        int resourceTypes,
        // fijo: elige un tipo de recurso al azar una vez. variable: elige un tipo al azar en cada vuelta
        int fixedProducers, int variableProducers,
        int fixedConsumers, int variableConsumers,
        int producerDelayMin, int producerDelayMax,
        int consumerDelayMin, int consumerDelayMax,
        int producerUnits, int consumerUnits,
        boolean producerUnlimited, int producerQuotaMin, int producerQuotaMax,
        boolean consumerUnlimited, int consumerQuotaMin, int consumerQuotaMax,
        boolean stockControl,
        boolean useWaitNotify, // false = sleep
        boolean synchronizedOps,
        int maxStock, int minStock) {

    // Lo que obliga a volver a crear los hilos (el resto se aplica en caliente)
    public boolean sameStructure(Settings o) {
        return resourceTypes == o.resourceTypes
                && fixedProducers == o.fixedProducers && variableProducers == o.variableProducers
                && fixedConsumers == o.fixedConsumers && variableConsumers == o.variableConsumers
                && producerUnlimited == o.producerUnlimited
                && producerQuotaMin == o.producerQuotaMin && producerQuotaMax == o.producerQuotaMax
                && consumerUnlimited == o.consumerUnlimited
                && consumerQuotaMin == o.consumerQuotaMin && consumerQuotaMax == o.consumerQuotaMax;
    }
}
