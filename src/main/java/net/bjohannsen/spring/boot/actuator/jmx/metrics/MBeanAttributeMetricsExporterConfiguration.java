package net.bjohannsen.spring.boot.actuator.jmx.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@ConfigurationProperties(prefix = "jmx-metrics-export")
public class MBeanAttributeMetricsExporterConfiguration {

    public static final long DEFAULT_SCRAPE_INTERVAL = 10_000;

    /**
     * Enables the jmx export of metrics.
     */
    private boolean enabled = true;

    /**
     * Prefix to use for exported metrics.
     */
    private String prefix = "jmx";

    /**
     * List of {@link MBeanAttributeMetricsConfig} to scrape metrics from.
     */
    private List<MBeanAttributeMetricsConfig> mbeans = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<MBeanAttributeMetricsConfig> getMbeans() {
        return mbeans;
    }

    public void setMbeans(List<MBeanAttributeMetricsConfig> mbeans) {
        this.mbeans = mbeans;
    }

    /**
     *
     */
    public static class MBeanAttributeMetricsConfig {

        /**
         * JMX object name of the MBean to scrape attributes from.
         */
        private String name;

        /**
         * Metric for the MBeans attributes.
         */
        private String metricName;

        /**
         * Set of attribute names to scrape from the MBean.
         */
        private Set<String> attributes;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMetricName() {
            return metricName;
        }

        public void setMetricName(String metricName) {
            this.metricName = metricName;
        }

        public Set<String> getAttributes() {
            return attributes;
        }

        public void setAttributes(Set<String> attributes) {
            this.attributes = attributes;
        }
    }
}
