package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Set;

/**
 * Configuration which JMX MBean attributes should be exported as metrics.
 *
 * An example file can be found be found in the test resources (/src/test/resources/mbean-metrics-config.json).
 */
public class JmxMetricsConfiguration {

    private final String prefix;
    private final List<MBeanMetricsConfig> mbeans;

    @JsonCreator
    public JmxMetricsConfiguration(
            @JsonProperty(value = "prefix", defaultValue = "jmx") String prefix,
            @JsonProperty("mbeans") List<MBeanMetricsConfig> mbeans) {
        this.prefix = prefix;
        this.mbeans = mbeans;
    }

    public String getPrefix() {
        return prefix;
    }

    public List<MBeanMetricsConfig> getMBeanConfigs() {
        return mbeans;
    }

    /**
     * Configuration for the attributes of MBeans attributes to export as metrics.
     */
    public static class MBeanMetricsConfig {

        /**
         * JMX object name of the MBean to scrape attributes from.
         */
        private final String mbeanName;

        /**
         * Metric for the MBeans attributes.
         */
        private final String metricName;

        /**
         * Set of attribute names to scrape from the MBean.
         */
        private final Set<String> attributes;

        @JsonCreator
        public MBeanMetricsConfig(@JsonProperty("mbeanName") String mbeanName,
                                  @JsonProperty("metricName") String metricName,
                                  @JsonProperty("attributes") Set<String> attributes) {
            this.mbeanName = mbeanName;
            this.metricName = metricName;
            this.attributes = attributes;
        }

        public String getMbeanName() {
            return mbeanName;
        }

        public String getMetricName() {
            return metricName;
        }

        public Set<String> getAttributes() {
            return attributes;
        }
    }

}
