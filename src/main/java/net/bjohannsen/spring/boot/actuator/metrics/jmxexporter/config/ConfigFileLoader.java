package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

/**
 * Loads multiple given resource links to configs and merges them.
 */
public class ConfigFileLoader {

    private static final Logger log = LoggerFactory.getLogger(ConfigFileLoader.class);

    private final ApplicationContext applicationContext;

    /**
     * Constructor.
     *
     * @param applicationContext current application context
     */
    public ConfigFileLoader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public JmxMetricsConfiguration loadConfigFile(String ... configFilePaths) {
        List<JmxMetricsConfiguration.MBeanMetricsConfig> mbeanConfigs = Arrays.stream(configFilePaths)
                .map(this::loadConfigFile)
                .map(JmxMetricsConfiguration::getMBeanConfigs)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return new JmxMetricsConfiguration(mbeanConfigs);

    }

    private JmxMetricsConfiguration loadConfigFile(String configFilePath) {
        try {
            Resource resource = applicationContext.getResource(configFilePath);
            ObjectMapper objectMapper = new ObjectMapper();
            JmxMetricsConfiguration jmxMetricsConfiguration = objectMapper.readValue(resource.getInputStream(), JmxMetricsConfiguration.class);
            if (log.isInfoEnabled()) {
                log.info("Loaded config file [{}].", configFilePath);
            }
            return jmxMetricsConfiguration;
        } catch (Exception e) {
            log.error("Unable to parse JMX metrics exporter config file [{}].", configFilePath, e);
            throw new RuntimeException(e);
        }
    }
}
