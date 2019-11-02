package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.MBeanMetricsExporterConfig;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReader;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics.MetricFacade;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Exporter for attributes of JMX MBeans.
 * <p>
 * In case of misconfiguration or missing beans, errors are logged and the attribute is skipped.
 */
public class MBeanAttributeMetricsExporter {

    private static final String DELIMITER = ".";

    private final MBeanAttributeReader mBeanAttributeReader;
    private final MetricFacade metricFacade;
    private final MBeanMetricsExporterConfig config;

    MBeanAttributeMetricsExporter(MBeanAttributeReader mBeanAttributeReader, MetricFacade metricFacade,
                                  MBeanMetricsExporterConfig config) {
        this.mBeanAttributeReader = mBeanAttributeReader;
        this.metricFacade = metricFacade;
        this.config = config;
    }

    /**
     * Reads values from JMX MBean attributes and submits them as metrics.
     */
    @Scheduled(fixedDelayString = "${jmx-metrics-export.scrape-interval:10000}",
            initialDelayString = "${jmx-metrics-export.scrape-interval:10000}")
    public void submitMetrics() {
        config.getMBeanConfigs().forEach(this::submitMBeanAttributesAsMetrics);
    }

    private void submitMBeanAttributesAsMetrics(MBeanMetricsExporterConfig.MBeanMetricsConfig mBeanConfiguration) {
        mBeanConfiguration.getAttributes().forEach(
                attributeName -> submitMBeanAttributeAsMetrics(mBeanConfiguration, attributeName));
    }

    private void submitMBeanAttributeAsMetrics(MBeanMetricsExporterConfig.MBeanMetricsConfig mBeanConfiguration, String attributeName) {
        String mBeanName = mBeanConfiguration.getMbeanName();
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
