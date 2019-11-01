package net.bjohannsen.spring.boot.actuator.jmx.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import net.bjohannsen.spring.boot.actuator.jmx.metrics.MBeanAttributeMetricsExporterConfiguration.MBeanAttributeMetricsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Exporter for attributes of JMX MBeans.
 *
 * In case of misconfiguration or missing beans, errors are logged and the attribute is skipped.
 */
class MBeanAttributeMetricsExporter {

    private static final Logger log = LoggerFactory.getLogger(MBeanAttributeMetricsExporter.class);
    private static final String DELIMITER = ".";

    private final MBeanAttributeReader mBeanAttributeReader;
    private final MeterRegistry meterRegistry;
    private final MBeanAttributeMetricsExporterConfiguration config;

    MBeanAttributeMetricsExporter(MBeanAttributeReader mBeanAttributeReader, MeterRegistry meterRegistry,
                                  MBeanAttributeMetricsExporterConfiguration config) {
        this.mBeanAttributeReader = mBeanAttributeReader;
        this.meterRegistry = meterRegistry;
        this.config = config;
    }

    /**
     * Fetches values from JMX and submits them to Micrometer metrics framework.
     */
    @Scheduled(fixedDelay = MBeanAttributeMetricsExporterConfiguration.DEFAULT_SCRAPE_INTERVAL)
    public void submitMetrics() {
        config.getMbeans().forEach(this::submitMBeanAttributesAsMetrics);
    }

    private void submitMBeanAttributesAsMetrics(MBeanAttributeMetricsConfig mBeanAttributeMetricsConfig) {
        mBeanAttributeMetricsConfig.getAttributes().forEach(
                attributeName -> submitMBeanAttributeAsMetrics(mBeanAttributeMetricsConfig, attributeName));
    }

    private void submitMBeanAttributeAsMetrics(MBeanAttributeMetricsConfig mBeanAttributeMetricsConfig, String attributeName) {
        String mBeanName = mBeanAttributeMetricsConfig.getName();
        mBeanAttributeReader.findMBeanAttributeValue(mBeanName, attributeName)
                .ifPresent(attributeValue -> {
                    String metricName = buildMetricName(mBeanAttributeMetricsConfig.getMetricName(), attributeName);
                    meterRegistry.gauge(metricName, attributeValue);
                    if (log.isDebugEnabled()) {
                        log.debug("Submitted metrics for MBean [{}], attribute [{}].", mBeanName, attributeName);
                    }
                });
    }

    private String buildMetricName(String mBeanName, String attributeName) {
        return config.getPrefix() + DELIMITER + mBeanName + DELIMITER + attributeName;
    }
}
