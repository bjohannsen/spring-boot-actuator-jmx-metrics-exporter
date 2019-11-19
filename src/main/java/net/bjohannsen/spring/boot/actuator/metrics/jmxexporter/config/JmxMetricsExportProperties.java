package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config;

import java.util.Set;
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
     * Prefix for exposed metrics.
     */
    private String prefix = "jmx";

    /**
     * List of paths to json config file containing {@link JmxMetricsConfiguration}.
     * Could be a references to files from the classpath (classpath:file.json), file system (file://file.json) or URLs.
     * If multiple configs are used, they are merged.
     */
    private Set<String> configFiles = Set.of("classpath:mbean-metrics-config.json");

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getScrapeInterval() {
        return scrapeInterval;
    }

    public void setScrapeInterval(long scrapeInterval) {
        this.scrapeInterval = scrapeInterval;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Set<String> getConfigFilePaths() {
        return configFiles;
    }

    public void setConfigFiles(Set<String> configFiles) {
        this.configFiles = configFiles;
    }
}
