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
        MBeanMetricsConfig firstMbeanConfig = jmxMetricsConfiguration.getMBeanConfigs().get(0);

        assertThat(firstMbeanConfig.getMbeanName(), equalTo("java.lang:type=Memory"));
        assertThat(firstMbeanConfig.getMetricName(), equalTo("memory"));
        assertThat(firstMbeanConfig.getAttributes(), containsInAnyOrder( JmxAttributeIdentifier.of("NonHeapMemoryUsage.max"), JmxAttributeIdentifier.of("HeapMemoryUsage.max")));

        MBeanMetricsConfig secondMbeanConfig = jmxMetricsConfiguration.getMBeanConfigs().get(1);
        assertThat(secondMbeanConfig.getMbeanName(), equalTo("net.bjohannsen.spring.boot.actuator.metrics.jmxexporter:name=mBean,type=IntegrationTestConfiguration.MBeanClass"));
        assertThat(secondMbeanConfig.getMetricName(), equalTo("testMetricA"));
        assertThat(secondMbeanConfig.getAttributes(), containsInAnyOrder(JmxAttributeIdentifier.of("SomeAttribute")));
    }
}
