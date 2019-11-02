package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = { JmxExporterIntegrationTest.TestConfiguration.class },
        initializers = {ConfigFileApplicationContextInitializer.class} )
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
public class JmxExporterIntegrationTest {

    private static final String EXPECTED_METRIC_NAME = "jmx.testMetricA.SomeAttribute";

    @Autowired
    private MeterRegistry meterRegistry;

    @EnableScheduling
    @EnableConfigurationProperties
    @Configuration
    @Import({JmxAutoConfiguration.class, JmxMetricsExporterAutoConfiguration.class})
    static class TestConfiguration {

        @MockBean
        MeterRegistry meterRegistry;

        @Bean
        MBeanClass mBean() {
            return new MBeanClass(42);
        }

        @Bean
        TaskScheduler taskScheduler() {
            return new ConcurrentTaskScheduler();
        }
    }

    @Test
    @DirtiesContext
    public void thatMbeanAttributesAreSubmittedAsMetrics() {
        verify(meterRegistry).gauge(EXPECTED_METRIC_NAME, 42.0d);
    }

    @Test
    @DirtiesContext
    public void thatMBeansAttributesAreSScrapeIntervalWorks() {
        // given
        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // then
        verify(meterRegistry, times(2)).gauge(EXPECTED_METRIC_NAME, 42.0d);
    }

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
    }
}
