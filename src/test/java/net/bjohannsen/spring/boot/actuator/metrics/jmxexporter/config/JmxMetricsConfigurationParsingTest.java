package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxMetricsConfiguration.MBeanMetricsConfig;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class JmxMetricsConfigurationParsingTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void thatConfigParsingWorks() throws IOException {
        // given
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("mbean-metrics-config.json");

        // when
        JmxMetricsConfiguration jmxMetricsConfiguration = objectMapper.readValue(resourceAsStream, JmxMetricsConfiguration.class);

        // then
        assertThat(jmxMetricsConfiguration.getPrefix(), equalTo("jmx"));
        MBeanMetricsConfig mBeanMetricsConfig = jmxMetricsConfiguration.getMBeanConfigs().get(0);

        assertThat(mBeanMetricsConfig.getMbeanName(), equalTo("net.bjohannsen.spring.boot.actuator.metrics.jmxexporter:name=mBean,type=IntegrationTestConfiguration.MBeanClass"));
        assertThat(mBeanMetricsConfig.getMetricName(), equalTo("testMetricA"));
        assertThat(mBeanMetricsConfig.getAttributes(), containsInAnyOrder("SomeAttribute"));
    }
}
