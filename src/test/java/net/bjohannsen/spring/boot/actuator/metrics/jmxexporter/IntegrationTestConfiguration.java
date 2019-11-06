package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@EnableScheduling
@EnableConfigurationProperties
@Configuration
@Import({JmxAutoConfiguration.class, JmxMetricsExporterAutoConfiguration.class, MetricsAutoConfiguration.class})
public class IntegrationTestConfiguration {

    @Bean
    MBeanClass mBean() {
        return new MBeanClass(42);
    }

    @Bean
    TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Bean
    MeterRegistry meterRegistry() { return new SimpleMeterRegistry(); }

    @ManagedResource
    public static class MBeanClass {

        private long someAttribute;

        public MBeanClass(long someAttribute) {
            this.someAttribute = someAttribute;
        }

        @ManagedAttribute
        public long getSomeAttribute() {
            return someAttribute;
        }

        public void setSomeAttribute(long someAttribute) {
            this.someAttribute = someAttribute;
        }
    }
}
