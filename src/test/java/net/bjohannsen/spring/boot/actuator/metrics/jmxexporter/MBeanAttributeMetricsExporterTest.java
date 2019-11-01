package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReader;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics.MetricFacade;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class MBeanAttributeMetricsExporterTest {

    private static final String M_BEAN_NAME = "mBeanName";
    private static final String METRIC_NAME = "metricName";
    private static final String ATTRIBUTE_A = "attributeA";
    private static final double ATTRIBUTE_VALUE = 19.09;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private MBeanAttributeReader mBeanAttributeReaderMock;

    @Mock
    private JmxMetricsExporterConfiguration configurationMock;

    @Mock
    private MetricFacade metricFacadeMock;

    @InjectMocks
    private MBeanAttributeMetricsExporter exporter;

    @Test
    public void thatSubmitMetricWorks() {
        // given
        JmxMetricsExporterConfiguration.MBeanConfiguration mbeanConfig = buildMBeanConfig(M_BEAN_NAME, METRIC_NAME, ATTRIBUTE_A);
        when(configurationMock.getPrefix()).thenReturn("jmx");
        when(configurationMock.getMbeans()).thenReturn(List.of(mbeanConfig));

        when(mBeanAttributeReaderMock.findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_A)).thenReturn(Optional.of(ATTRIBUTE_VALUE));

        // when
        exporter.submitMetrics();

        // then
        verify(metricFacadeMock).submitGauge("jmx." + METRIC_NAME + "." + ATTRIBUTE_A, ATTRIBUTE_VALUE);
    }

    @Test
    public void thatNoMetricIsSubmittedWhenNoJmxAttributeValueIsAvailable() {
        // given
        JmxMetricsExporterConfiguration.MBeanConfiguration mbeanConfig = buildMBeanConfig(M_BEAN_NAME, METRIC_NAME, ATTRIBUTE_A);
        when(configurationMock.getMbeans()).thenReturn(List.of(mbeanConfig));

        when(mBeanAttributeReaderMock.findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_A)).thenReturn(Optional.empty());

        // when
        exporter.submitMetrics();

        // then
        verifyZeroInteractions(metricFacadeMock);
    }

    @Test
    public void thatSubmittingOfMultipleAttributesForEachMBeanWorks() {
        // given
        String attributeB = "attributeB";
        double valueB = 2011;
        JmxMetricsExporterConfiguration.MBeanConfiguration mbeanConfig = buildMBeanConfig(M_BEAN_NAME, METRIC_NAME, ATTRIBUTE_A, attributeB);
        when(configurationMock.getMbeans()).thenReturn(List.of(mbeanConfig));
        when(configurationMock.getPrefix()).thenReturn("jmx");

        doReturn(Optional.of(ATTRIBUTE_VALUE)).when(mBeanAttributeReaderMock).findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_A);
        doReturn(Optional.of(valueB)).when(mBeanAttributeReaderMock).findMBeanAttributeValue(M_BEAN_NAME, attributeB);

        // when
        exporter.submitMetrics();

        // then
        verify(metricFacadeMock).submitGauge("jmx." + METRIC_NAME + "." + ATTRIBUTE_A, ATTRIBUTE_VALUE);
        verify(metricFacadeMock).submitGauge("jmx." + METRIC_NAME + "." + attributeB, valueB);
    }

    @Test
    public void thatSubmittingOfMultipleBeansWorks() {
        // given
        String beanNameB = "beanNameB";
        String metricNameB = "metricNameB";
        double valueB = 2011;
        JmxMetricsExporterConfiguration.MBeanConfiguration mbeanConfig = buildMBeanConfig(M_BEAN_NAME, METRIC_NAME, ATTRIBUTE_A);
        JmxMetricsExporterConfiguration.MBeanConfiguration mbeanConfigB = buildMBeanConfig(beanNameB, metricNameB, ATTRIBUTE_A);
        when(configurationMock.getMbeans()).thenReturn(List.of(mbeanConfig, mbeanConfigB));
        when(configurationMock.getPrefix()).thenReturn("jmx");

        doReturn(Optional.of(ATTRIBUTE_VALUE)).when(mBeanAttributeReaderMock).findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_A);
        doReturn(Optional.of(valueB)).when(mBeanAttributeReaderMock).findMBeanAttributeValue(beanNameB, ATTRIBUTE_A);

        // when
        exporter.submitMetrics();

        // then
        verify(metricFacadeMock).submitGauge("jmx." + METRIC_NAME + "." + ATTRIBUTE_A, ATTRIBUTE_VALUE);
        verify(metricFacadeMock).submitGauge("jmx." + metricNameB + "." + ATTRIBUTE_A, valueB);
    }

    private JmxMetricsExporterConfiguration.MBeanConfiguration buildMBeanConfig(String beanName, String metricName, String ... attributes) {
        JmxMetricsExporterConfiguration.MBeanConfiguration configuration = new JmxMetricsExporterConfiguration.MBeanConfiguration();
        configuration.setName(beanName);
        configuration.setMetricName(metricName);
        configuration.getAttributes().addAll(Arrays.asList(attributes));
        return configuration;
    }
}