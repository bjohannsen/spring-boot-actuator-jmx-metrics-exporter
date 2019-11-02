package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configure which MBeans attributes to export as metrics.
 */
@Component
@ConfigurationProperties(prefix = "jmx-metrics-export")
public class JmxMetricsExporterConfiguration {

    /**
     * Enables the jmx export of metrics.
     */
    private boolean enabled = true;

    /**
     * Prefix to use for exported metrics.
     */
    private String prefix = "jmx";

    /**
     * Scrape interval in milliseconds.
     */
    private long scrapeInterval = 10_000;

    /**
     * List of {@link MBeanConfiguration} to scrape metrics from.
     */
    private final List<MBeanConfiguration> mbeans = new ArrayList<>();

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

    public long getScrapeInterval() {
        return scrapeInterval;
    }

    public void setScrapeInterval(long scrapeInterval) {
        this.scrapeInterval = scrapeInterval;
    }

    public List<MBeanConfiguration> getMbeans() {
        return mbeans;
    }

    /**
     *
     */
    public static class MBeanConfiguration {

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
        private final Set<String> attributes = new HashSet<>();

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setMetricName(String metricName) {
            this.metricName = metricName;
        }

        public String getMetricName() {
            return metricName;
        }

        public Set<String> getAttributes() {
            return attributes;
        }
    }
}
