package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Facade for metrics collection.
 */
public class MetricFacade {

    private final ValueReferenceStore valueReferenceStore;
    private final MeterRegistry meterRegistry;

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
        meterRegistry.gauge(metricName, value);
    }
}
