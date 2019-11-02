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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = { IntegrationTestConfiguration.class },
        initializers = {ConfigFileApplicationContextInitializer.class} )
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
public class JmxExporterIntegrationTest {

    private static final String EXPECTED_METRIC_NAME = "jmx.testMetricA.SomeAttribute";
    private static final long STARTUP_DELAY = 100;
    private static final long SCRAPE_INTERVAL = 1000;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    @DirtiesContext
    public void thatMbeanAttributesAreSubmittedAsMetrics() {
        // when
        waitFor(STARTUP_DELAY);
        waitFor(SCRAPE_INTERVAL);

        // then
        assertThat(meterRegistry.get(EXPECTED_METRIC_NAME).gauge().value(), equalTo(42.0d));
    }

    /*
     * Test runs with a scrape interval of 1000ms.
     */
    @Test
    @DirtiesContext
    public void thatMBeansAttributesAreSScrapeIntervalWorks() {
        // given
        waitFor(STARTUP_DELAY);
        waitFor(SCRAPE_INTERVAL);
        assertThat(meterRegistry.get(EXPECTED_METRIC_NAME).gauge().value(), equalTo(42.0d));

        // then
        waitFor(SCRAPE_INTERVAL);
        assertThat(meterRegistry.get(EXPECTED_METRIC_NAME).gauge().value(), equalTo(43.0d));
    }

    private void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
