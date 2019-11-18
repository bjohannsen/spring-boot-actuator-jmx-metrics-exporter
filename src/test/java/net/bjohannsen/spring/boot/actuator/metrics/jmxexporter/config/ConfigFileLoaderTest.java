package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.FileNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class ConfigFileLoaderTest {

    private static final String NON_EXISTING_CONFIG_FILE_PATH = "classpath:some-config.json";
    private static final String EXISTING_CONFIG_FILE_PATH = "classpath:mbean-metrics-config.json";
    private static final String ANOTHER_CONFIG_FILE_PATH = "classpath:second-metrics-config.json";

    private static final String VALID_CONFIG = "{\n" +
            "  \"mbeans\": [\n" +
            "    {\n" +
            "      \"mbeanName\": \"net.bjohannsen.spring.boot.actuator.metrics.jmxexporter:name=mBean,type=IntegrationTestConfiguration.MBeanClass\",\n" +
            "      \"metricName\": \"testMetricA\",\n" +
            "      \"attributes\": [\"SomeAttribute\"]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private static final String ANOTHER_CONFIG = "{\n" +
            "  \"mbeans\": [\n" +
            "    {\n" +
            "      \"mbeanName\": \"java.lang:type=Memory\",\n" +
            "      \"metricName\": \"memory\",\n" +
            "      \"attributes\": [\"HeapMemoryUsage.max\", \"NonHeapMemoryUsage.max\"]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ApplicationContext applicationContextMock;

    @InjectMocks
    private ConfigFileLoader configFileLoader;

    @Test
    public void thatExceptionIsRaisedWhenConfigFileIsNotFound() {
        // given
        Resource testResource = new ClassPathResource(NON_EXISTING_CONFIG_FILE_PATH);

        when(applicationContextMock.getResource(NON_EXISTING_CONFIG_FILE_PATH)).thenReturn(testResource);

        // then
        expectedException.expect(RuntimeException.class);
        expectedException.expectCause(isA(FileNotFoundException.class));

        // when
        JmxMetricsConfiguration loadedConfig = configFileLoader.loadConfigFile(NON_EXISTING_CONFIG_FILE_PATH);
    }

    @Test
    public void thatLoadingInvalidJsonRaisesException() {
        // given
        Resource testResource = new ByteArrayResource("{\"some\":\"invalidConfig\"}".getBytes());

        when(applicationContextMock.getResource(EXISTING_CONFIG_FILE_PATH)).thenReturn(testResource);

        // then
        expectedException.expect(RuntimeException.class);
        expectedException.expectCause(isA(JsonMappingException.class));

        // when
        configFileLoader.loadConfigFile(EXISTING_CONFIG_FILE_PATH);
    }

    @Test
    public void thatLoadingWorksForExistingResourceWithValidJson() {
        // given
        Resource testResource = new ByteArrayResource(VALID_CONFIG.getBytes());

        when(applicationContextMock.getResource(EXISTING_CONFIG_FILE_PATH)).thenReturn(testResource);

        // when
        JmxMetricsConfiguration loadedConfig = configFileLoader.loadConfigFile(EXISTING_CONFIG_FILE_PATH);

        // then
        assertThat(loadedConfig.getMBeanConfigs().get(0).getMetricName(), equalTo("testMetricA"));
    }

    @Test
    public void thatLoadingWorksForMultipleConfigs() {
        // given
        Resource testResource = new ByteArrayResource(VALID_CONFIG.getBytes());
        Resource anotherResource = new ByteArrayResource(ANOTHER_CONFIG.getBytes());
        doReturn(testResource).when(applicationContextMock).getResource(EXISTING_CONFIG_FILE_PATH);
        doReturn(anotherResource).when(applicationContextMock).getResource(ANOTHER_CONFIG_FILE_PATH);

        // when
        JmxMetricsConfiguration loadedConfig = configFileLoader.loadConfigFile(EXISTING_CONFIG_FILE_PATH, ANOTHER_CONFIG_FILE_PATH);

        // then
        assertThat(loadedConfig.getMBeanConfigs().get(0).getMetricName(), equalTo("testMetricA"));
        assertThat(loadedConfig.getMBeanConfigs().get(1).getMetricName(), equalTo("memory"));
    }
}