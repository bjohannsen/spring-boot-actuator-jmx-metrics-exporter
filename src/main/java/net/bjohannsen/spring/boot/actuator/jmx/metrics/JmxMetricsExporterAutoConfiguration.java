package net.bjohannsen.spring.boot.actuator.jmx.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import javax.management.MBeanServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration for the JMX MBean metrics exporter.
 * Beans will not be created if property "jmx-metrics-exporter.enabled" is set to false.
 */
@EnableScheduling
@ConditionalOnProperty(value = "jmx-metrics-export.enabled", havingValue = "true")
@ComponentScan(basePackageClasses = JmxMetricsExporterAutoConfiguration.class)
@Configuration
class JmxMetricsExporterAutoConfiguration {

    @Bean
    MBeanAttributeMetricsExporter jmxBeanMetricsExporter(MBeanAttributeReader mBeanAttributeReader, MeterRegistry meterRegistry,
                                                        MBeanAttributeMetricsExporterConfiguration configuration) {
        return new MBeanAttributeMetricsExporter(mBeanAttributeReader, meterRegistry, configuration);
    }

    @Bean
    MBeanAttributeReader mBeanAttributeReader(MBeanServer mBeanServer) {
        return new MBeanAttributeReader(mBeanServer);
    }
}
