package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import javax.management.MBeanServer;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.MBeanMetricsExporterConfig;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.MBeanAttributeMetricsExporterProperties;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReader;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics.MetricFacade;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics.ValueReferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;

import static net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.MBeanAttributeMetricsExporterProperties.CONFIG_BASE_NAME;

/**
 * Configuration for the JMX MBean metrics exporter.
 * Beans will not be created if property "jmx-metrics-exporter.enabled" is set to false.
 */
@EnableScheduling
@ConditionalOnProperty(value = CONFIG_BASE_NAME + ".enabled", havingValue = "true")
@ComponentScan(basePackageClasses = JmxMetricsExporterAutoConfiguration.class)
@Configuration
class JmxMetricsExporterAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JmxMetricsExporterAutoConfiguration.class);

    @Bean
    MBeanAttributeMetricsExporter jmxBeanMetricsExporter(MBeanAttributeReader mBeanAttributeReader,
                                                         MetricFacade metricFacade,
                                                         MBeanMetricsExporterConfig configuration) {
        return new MBeanAttributeMetricsExporter(mBeanAttributeReader, metricFacade, configuration);
    }

    @Bean
    MBeanAttributeReader mBeanAttributeReader(MBeanServer mBeanServer) {
        return new MBeanAttributeReader(mBeanServer);
    }

    @Bean
    MetricFacade metricFacade(MeterRegistry meterRegistry) {
        return new MetricFacade(new ValueReferenceStore(), meterRegistry);
    }

    @Bean
    MBeanMetricsExporterConfig loadConfigFile(ApplicationContext context, MBeanAttributeMetricsExporterProperties configuration) {
        try {
            String configFilePath = configuration.getConfigFile();
            Resource resource = context.getResource(configFilePath);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(resource.getInputStream(), MBeanMetricsExporterConfig.class);
        } catch (Exception e) {
            log.error("Unable to parse config file. No MBean metrics will be exported", e);
            return new MBeanMetricsExporterConfig("jmx", List.of());
        }
    }
}
