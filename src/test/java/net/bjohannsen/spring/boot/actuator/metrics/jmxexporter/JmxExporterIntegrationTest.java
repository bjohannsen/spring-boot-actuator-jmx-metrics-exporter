package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = { IntegrationTestConfiguration.class },
        initializers = {ConfigFileApplicationContextInitializer.class} )
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
public class JmxExporterIntegrationTest {

    private static final String EXPECTED_METRIC_NAME = "jmx.testMetricA.SomeAttribute";

    @Autowired
    private MeterRegistry meterRegistry;
    public static final long SCRAPE_INTERVAL = 1000;

    @Test
    @DirtiesContext
    public void thatMbeanAttributesAreSubmittedAsMetrics() {
        // when
        waitFor(SCRAPE_INTERVAL);

        // then
        verify(meterRegistry).gauge(EXPECTED_METRIC_NAME, 42.0d);
    }

    /*
     * Test runs with a scrape interval of 1000ms.
     */
    @Test
    @DirtiesContext
    public void thatMBeansAttributesAreSScrapeIntervalWorks() {
        // given
        int expectedNumberOfCalls = 2;

        // when
        waitFor(SCRAPE_INTERVAL * expectedNumberOfCalls + (SCRAPE_INTERVAL/2) );

        // then
        verify(meterRegistry, times(expectedNumberOfCalls)).gauge(EXPECTED_METRIC_NAME, 42.0d);
    }

    private void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
