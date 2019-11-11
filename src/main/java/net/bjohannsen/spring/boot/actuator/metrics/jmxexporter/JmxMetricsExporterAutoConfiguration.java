package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import java.io.IOException;
import java.util.List;
import javax.management.MBeanServer;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxMetricsConfiguration;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxMetricsExportProperties;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.AttributeParserRegistry;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.parser.CompositeTypeAttributeParser;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.parser.AttributeValueParser;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReader;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.parser.SimpleTypeAttributeParser;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics.MetricFacade;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics.ValueReferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;

import static net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxMetricsExportProperties.CONFIG_BASE_NAME;

/**
 * Configuration for the JMX MBean metrics exporter.
 * Beans will not be created if property "jmx-metrics-exporter.enabled" is set to false.
 */
@EnableScheduling
@ConditionalOnProperty(value = CONFIG_BASE_NAME + ".enabled", havingValue = "true")
@AutoConfigureAfter(JmxMetricsExportProperties.class)
@Configuration
class JmxMetricsExporterAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JmxMetricsExporterAutoConfiguration.class);

    @Bean
    JmxMetricsExporter jmxBeanMetricsExporter(MBeanAttributeReader mBeanAttributeReader,
                                              MetricFacade metricFacade,
                                              JmxMetricsConfiguration configuration) {
        return new JmxMetricsExporter(mBeanAttributeReader, metricFacade, configuration);
    }

    @Bean
    MBeanAttributeReader mBeanAttributeReader(MBeanServer mBeanServer, AttributeParserRegistry attributeParserRegistry) {
        return new MBeanAttributeReader(mBeanServer, attributeParserRegistry);
    }

    @Bean
    AttributeParserRegistry attributeParserRegistry(List<AttributeValueParser> attributeParsers) {
        return new AttributeParserRegistry(attributeParsers);
    }

    @Bean
    CompositeTypeAttributeParser compositeTypeJmxAttributeReader() {
        return new CompositeTypeAttributeParser();
    }

    @Bean
    SimpleTypeAttributeParser simpleTypeJmxAttributeReader() {
        return new SimpleTypeAttributeParser();
    }

    @Bean
    MetricFacade metricFacade(MeterRegistry meterRegistry) {
        return new MetricFacade(new ValueReferenceStore(), meterRegistry);
    }

    @Bean
    JmxMetricsExportProperties jmxMetricsExportProperties() {
        return new JmxMetricsExportProperties();
    }

    @Bean
    JmxMetricsConfiguration loadConfigFile(ApplicationContext context, JmxMetricsExportProperties configuration) throws IOException {
        try {
            String configFilePath = configuration.getConfigFile();
            Resource resource = context.getResource(configFilePath);
            ObjectMapper objectMapper = new ObjectMapper();
            JmxMetricsConfiguration jmxMetricsConfiguration = objectMapper.readValue(resource.getInputStream(), JmxMetricsConfiguration.class);
            if (log.isInfoEnabled()) {
                log.info("Loaded JMX Metrics exporter with config file [{}].", configuration.getConfigFile());
            }
            return jmxMetricsConfiguration;
        } catch (Exception e) {
            log.error("Unable to parse JMX metrics exporter config file [{}].", configuration.getConfigFile(), e);
            throw e;
        }
    }
}
