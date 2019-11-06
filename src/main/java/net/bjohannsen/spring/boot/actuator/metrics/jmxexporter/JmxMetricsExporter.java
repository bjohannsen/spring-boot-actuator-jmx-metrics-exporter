package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxMetricsConfiguration;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReader;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics.MetricFacade;
import org.springframework.scheduling.annotation.Scheduled;

import static net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxMetricsExportProperties.CONFIG_BASE_NAME;

/**
 * Exporter for attributes of JMX MBeans.
 * <p>
 * In case of misconfiguration or missing beans, errors are logged and the attribute is skipped.
 */
public class JmxMetricsExporter {

    private static final String DELIMITER = ".";

    private final MBeanAttributeReader mBeanAttributeReader;
    private final MetricFacade metricFacade;
    private final JmxMetricsConfiguration config;

    JmxMetricsExporter(MBeanAttributeReader mBeanAttributeReader, MetricFacade metricFacade,
                       JmxMetricsConfiguration config) {
        this.mBeanAttributeReader = mBeanAttributeReader;
        this.metricFacade = metricFacade;
        this.config = config;
    }

    /**
     * Reads values from JMX MBean attributes and submits them as metrics.
     */
    @Scheduled(fixedDelayString = "${" + CONFIG_BASE_NAME + ".scrape-interval:10000}",
            initialDelayString = "${" + CONFIG_BASE_NAME + ".scrape-interval:10000}")
    public void submitMetrics() {
        config.getMBeanConfigs().forEach(this::submitMBeanAttributesAsMetrics);
    }

    private void submitMBeanAttributesAsMetrics(JmxMetricsConfiguration.MBeanMetricsConfig mBeanConfiguration) {
        mBeanConfiguration.getAttributes().forEach(
                attributeName -> submitMBeanAttributeAsMetrics(mBeanConfiguration, attributeName));
    }

    private void submitMBeanAttributeAsMetrics(JmxMetricsConfiguration.MBeanMetricsConfig mBeanConfiguration, String attributeName) {
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
