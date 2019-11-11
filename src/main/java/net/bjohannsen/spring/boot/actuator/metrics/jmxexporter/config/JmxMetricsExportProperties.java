package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the JMX metrics exporter.
 */
@Component
@ConfigurationProperties(prefix = JmxMetricsExportProperties.CONFIG_BASE_NAME)
public class JmxMetricsExportProperties {

    /**
     * Root path for all properties.
     */
    public static final String CONFIG_BASE_NAME = "jmx-metrics-export";

    /**
     * Enables the jmx export of metrics.
     */
    private boolean enabled = false;

    /**
     * Scrape interval in milliseconds.
     */
    private long scrapeInterval = 10_000;

    /**
     * Path to json config file containing {@link JmxMetricsConfiguration}.
     * Could be a reference to a file from the classpath (classpath:file.json), file system (file://file.json) or an URL.
     */
    private String configFile = "classpath:mbean-metrics-config.json";

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setScrapeInterval(long scrapeInterval) {
        this.scrapeInterval = scrapeInterval;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }
}
