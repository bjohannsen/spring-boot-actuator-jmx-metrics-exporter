package net.bjohannsen.spring.boot.actuator.jmx.metrics;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Micrometer relies on weak references for gauges.
 * This class is used to hold and store references to values to save them from GC.
 */
public class MetricFacade {

    private final ValueReferenceStore valueReferenceStore;
    private final MeterRegistry meterRegistry;

    public MetricFacade(ValueReferenceStore valueReferenceStore, MeterRegistry meterRegistry) {
        this.valueReferenceStore = valueReferenceStore;
        this.meterRegistry = meterRegistry;
    }

    /**
     *
     * @param metricName
     * @param value
     */
    public void submitGauge(String metricName, Double value) {
        valueReferenceStore.keepReference(metricName, value);
        meterRegistry.gauge(metricName, value);
    }
}
