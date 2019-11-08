package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxMetricsConfiguration;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxMetricsExportProperties;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class JmxMetricsExporterAutoConfigurationTest {

    private static final String NON_EXISTING_CONFIG_FILE_PATH = "classpath:some-config.json";
    private static final String EXISTING_CONFIG_FILE_PATH = "classpath:mbean-metrics-config.json";

    private static final String VALID_CONFIG = "{\n" +
            "  \"prefix\": \"jmx\",\n" +
            "  \"mbeans\": [\n" +
            "    {\n" +
            "      \"mbeanName\": \"net.bjohannsen.spring.boot.actuator.metrics.jmxexporter:name=mBean,type=IntegrationTestConfiguration.MBeanClass\",\n" +
            "      \"metricName\": \"testMetricA\",\n" +
            "      \"attributes\": [\"SomeAttribute\"]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private ApplicationContext applicationContextMock;

    private JmxMetricsExporterAutoConfiguration autoConfiguration = new JmxMetricsExporterAutoConfiguration();

    @Test(expected = FileNotFoundException.class)
    public void thatExceptionIsRaisedWhenConfigFileIsNotFound() throws IOException {
        // given
        JmxMetricsExportProperties properties = new JmxMetricsExportProperties();
        properties.setConfigFile(NON_EXISTING_CONFIG_FILE_PATH);
        Resource testResource = new ClassPathResource(NON_EXISTING_CONFIG_FILE_PATH);

        when(applicationContextMock.getResource(NON_EXISTING_CONFIG_FILE_PATH)).thenReturn(testResource);

        // when
        JmxMetricsConfiguration loadedConfig = autoConfiguration.loadConfigFile(applicationContextMock, properties);

        // then -> FileNotFoundException
    }

    @Test(expected = JsonMappingException.class)
    public void thatLoadingInvalidJsonRaisesException() throws IOException {
        // given
        JmxMetricsExportProperties properties = new JmxMetricsExportProperties();
        properties.setConfigFile(EXISTING_CONFIG_FILE_PATH);
        Resource testResource = new ByteArrayResource("{\"some\":\"invalidConfig\"}".getBytes());

        when(applicationContextMock.getResource(EXISTING_CONFIG_FILE_PATH)).thenReturn(testResource);

        // when
        autoConfiguration.loadConfigFile(applicationContextMock, properties);

        // then -> Exception
    }

    @Test
    public void thatLoadingWorksForExistingResourceWithValidJson() throws IOException {
        // given
        JmxMetricsExportProperties properties = new JmxMetricsExportProperties();
        properties.setConfigFile(EXISTING_CONFIG_FILE_PATH);
        Resource testResource = new ByteArrayResource(VALID_CONFIG.getBytes());

        when(applicationContextMock.getResource(EXISTING_CONFIG_FILE_PATH)).thenReturn(testResource);

        // when
        JmxMetricsConfiguration loadedConfig = autoConfiguration.loadConfigFile(applicationContextMock, properties);

        // then
        assertThat(loadedConfig.getPrefix(), equalTo("jmx"));
        assertThat(loadedConfig.getMBeanConfigs().get(0).getMetricName(), equalTo("testMetricA"));
    }
}
