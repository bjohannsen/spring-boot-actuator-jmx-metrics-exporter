package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Facade for metrics collection.
 * Wraps micrometers {@link MeterRegistry} and keeps strong references to all values passed.
 */
public class MetricFacade {

    private final ValueReferenceStore valueReferenceStore;
    private final MeterRegistry meterRegistry;

    /**
     * Constructor.
     *
     * @param valueReferenceStore reference store to hold value references
     * @param meterRegistry Micrometer meterRegistry bean
     */
    public MetricFacade(ValueReferenceStore valueReferenceStore, MeterRegistry meterRegistry) {
        this.valueReferenceStore = valueReferenceStore;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Submits a new value for the given metric name.
     *
     * @param metricName given metricName
     * @param value given value
     */
    public void submitGauge(String metricName, Double value) {
        valueReferenceStore.keepReference(metricName, value);
        meterRegistry.gauge(metricName, valueReferenceStore, referenceStore -> referenceStore.getValue(metricName));
    }
}
