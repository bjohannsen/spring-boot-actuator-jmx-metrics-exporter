package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxMetricsConfiguration;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxAttributeIdentifier;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReader;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReadException;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics.MetricFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import static net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxMetricsExportProperties.CONFIG_BASE_NAME;

/**
 * Exporter for attributes of JMX MBeans.
 * <p>
 * In case of misconfiguration or missing beans, errors are logged and the attribute is skipped.
 */
public class JmxMetricsExporter {

    private static final Logger log = LoggerFactory.getLogger(JmxMetricsExporter.class);

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
                attributeId -> submitMBeanAttributeAsMetrics(mBeanConfiguration, attributeId));
    }

    private void submitMBeanAttributeAsMetrics(JmxMetricsConfiguration.MBeanMetricsConfig mBeanConfiguration, JmxAttributeIdentifier attributeId) {
        String mBeanName = mBeanConfiguration.getMbeanName();
        try {
            double attributeValue = mBeanAttributeReader.findMBeanAttributeValue(mBeanName, attributeId);
            String metricName = buildMetricName(mBeanConfiguration.getMetricName(), attributeId);
            metricFacade.submitGauge(metricName, attributeValue);
        } catch (MBeanAttributeReadException e) {
            if (log.isDebugEnabled()) {
                log.debug("Unable to read attribute [" + attributeId + "]from MBean [" + mBeanName + "].", e);
            }
        }
    }

    private String buildMetricName(String mBeanName, JmxAttributeIdentifier attributeId) {
        return config.getPrefix() + DELIMITER + mBeanName + DELIMITER + attributeId;
    }
}
