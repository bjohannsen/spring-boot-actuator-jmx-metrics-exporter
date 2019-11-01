package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.JmxMetricsExporterConfiguration.MBeanConfiguration;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReader;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics.MetricFacade;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Exporter for attributes of JMX MBeans.
 *
 * In case of misconfiguration or missing beans, errors are logged and the attribute is skipped.
 */
class MBeanAttributeMetricsExporter {

    private static final String DELIMITER = ".";

    private final MBeanAttributeReader mBeanAttributeReader;
    private final MetricFacade metricFacade;
    private final JmxMetricsExporterConfiguration config;

    MBeanAttributeMetricsExporter(MBeanAttributeReader mBeanAttributeReader, MetricFacade metricFacade,
                                  JmxMetricsExporterConfiguration config) {
        this.mBeanAttributeReader = mBeanAttributeReader;
        this.metricFacade = metricFacade;
        this.config = config;
    }

    /**
     * Fetches values from JMX and submits them to Micrometer metrics framework.
     */
    @Scheduled(fixedDelay = JmxMetricsExporterConfiguration.DEFAULT_SCRAPE_INTERVAL)
    public void submitMetrics() {
        config.getMbeans().forEach(this::submitMBeanAttributesAsMetrics);
    }

    private void submitMBeanAttributesAsMetrics(MBeanConfiguration mBeanConfiguration) {
        mBeanConfiguration.getAttributes().forEach(
                attributeName -> submitMBeanAttributeAsMetrics(mBeanConfiguration, attributeName));
    }

    private void submitMBeanAttributeAsMetrics(MBeanConfiguration mBeanConfiguration, String attributeName) {
        String mBeanName = mBeanConfiguration.getName();
        mBeanAttributeReader.findMBeanAttributeValue(mBeanName, attributeName)
                .ifPresent(attributeValue -> {
                    String metricName = buildMetricName(mBeanConfiguration.getMetricName(), attributeName);
                    metricFacade.submitGauge(metricName, attributeValue);
                });
    }

    private String buildMetricName(String mBeanName, String attributeName) {
        return config.getPrefix() + DELIMITER + mBeanName + DELIMITER + attributeName;
    }
}
