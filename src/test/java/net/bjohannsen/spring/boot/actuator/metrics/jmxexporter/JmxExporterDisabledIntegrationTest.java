package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = { IntegrationTestConfiguration.class },
        initializers = {ConfigFileApplicationContextInitializer.class} )
@TestPropertySource(properties = { "spring.config.location=classpath:application-disabled-test.yml" })
public class JmxExporterDisabledIntegrationTest {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private ApplicationContext applicationContext;

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void thatNoExporterBeanIsCreated() {
        MBeanAttributeMetricsExporter bean = applicationContext.getBean(MBeanAttributeMetricsExporter.class);
    }

    /*
     * Test runs with a scrape interval of 1000ms.
     */
    @Test
    @DirtiesContext
    public void thatNoMetricsAreSubmittedIfLibraryIsDisabled() {
        // given
        long scrapeInterval = 1000;
        int expectedNumberOfCalls = 2;

        // when
        try {
            Thread.sleep(scrapeInterval * expectedNumberOfCalls);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // then
        verifyZeroInteractions(meterRegistry);
    }
}
