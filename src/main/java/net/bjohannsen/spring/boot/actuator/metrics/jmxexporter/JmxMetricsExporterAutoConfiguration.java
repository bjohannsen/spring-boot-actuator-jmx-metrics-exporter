package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import io.micrometer.core.instrument.MeterRegistry;
import javax.management.MBeanServer;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReader;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics.MetricFacade;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics.ValueReferenceStore;
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
    MBeanAttributeMetricsExporter jmxBeanMetricsExporter(MBeanAttributeReader mBeanAttributeReader,
                                                         MetricFacade metricFacade,
                                                         JmxMetricsExporterConfiguration configuration) {
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
}
