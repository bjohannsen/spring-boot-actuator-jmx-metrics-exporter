package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter;

import java.util.List;
import java.util.Set;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxMetricsConfiguration;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxAttributeIdentifier;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReader;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReadException;
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

public class JmxMetricsExporterTest {

    private static final String M_BEAN_NAME = "mBeanName";
    private static final String METRIC_NAME = "metricName";
    private static final JmxAttributeIdentifier ATTRIBUTE_A = JmxAttributeIdentifier.of("attributeA");
    private static final double ATTRIBUTE_VALUE = 19.09;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private MBeanAttributeReader mBeanAttributeReaderMock;

    @Mock
    private JmxMetricsConfiguration configurationMock;

    @Mock
    private MetricFacade metricFacadeMock;

    @InjectMocks
    private JmxMetricsExporter exporter;

    @Test
    public void thatSubmitMetricWorks() {
        // given

        JmxMetricsConfiguration.MBeanMetricsConfig mbeanConfig = buildMBeanConfig(M_BEAN_NAME, METRIC_NAME, ATTRIBUTE_A);
        when(configurationMock.getPrefix()).thenReturn("jmx");
        when(configurationMock.getMBeanConfigs()).thenReturn(List.of(mbeanConfig));

        when(mBeanAttributeReaderMock.findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_A)).thenReturn(ATTRIBUTE_VALUE);

        // when
        exporter.submitMetrics();

        // then
        verify(metricFacadeMock).submitGauge("jmx." + METRIC_NAME + "." + ATTRIBUTE_A, ATTRIBUTE_VALUE);
    }

    @Test
    public void thatNoMetricIsSubmittedWhenNoJmxAttributeValueIsAvailable() {
        // given
        JmxMetricsConfiguration.MBeanMetricsConfig mbeanConfig = buildMBeanConfig(M_BEAN_NAME, METRIC_NAME, ATTRIBUTE_A);
        when(configurationMock.getMBeanConfigs()).thenReturn(List.of(mbeanConfig));

        when(mBeanAttributeReaderMock.findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_A)).thenThrow(new MBeanAttributeReadException(""));

        // when
        exporter.submitMetrics();

        // then
        verifyZeroInteractions(metricFacadeMock);
    }

    @Test
    public void thatSubmittingOfMultipleAttributesForEachMBeanWorks() {
        // given
        JmxAttributeIdentifier attributeB = JmxAttributeIdentifier.of("attributeB");
        double valueB = 2011;
        JmxMetricsConfiguration.MBeanMetricsConfig mbeanConfig = buildMBeanConfig(M_BEAN_NAME, METRIC_NAME, ATTRIBUTE_A, attributeB);
        when(configurationMock.getMBeanConfigs()).thenReturn(List.of(mbeanConfig));
        when(configurationMock.getPrefix()).thenReturn("jmx");

        doReturn(ATTRIBUTE_VALUE).when(mBeanAttributeReaderMock).findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_A);
        doReturn(valueB).when(mBeanAttributeReaderMock).findMBeanAttributeValue(M_BEAN_NAME, attributeB);

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
        JmxMetricsConfiguration.MBeanMetricsConfig mbeanConfig = buildMBeanConfig(M_BEAN_NAME, METRIC_NAME, ATTRIBUTE_A);
        JmxMetricsConfiguration.MBeanMetricsConfig mbeanConfigB = buildMBeanConfig(beanNameB, metricNameB, ATTRIBUTE_A);
        when(configurationMock.getMBeanConfigs()).thenReturn(List.of(mbeanConfig, mbeanConfigB));
        when(configurationMock.getPrefix()).thenReturn("jmx");

        doReturn(ATTRIBUTE_VALUE).when(mBeanAttributeReaderMock).findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_A);
        doReturn(valueB).when(mBeanAttributeReaderMock).findMBeanAttributeValue(beanNameB, ATTRIBUTE_A);

        // when
        exporter.submitMetrics();

        // then
        verify(metricFacadeMock).submitGauge("jmx." + METRIC_NAME + "." + ATTRIBUTE_A, ATTRIBUTE_VALUE);
        verify(metricFacadeMock).submitGauge("jmx." + metricNameB + "." + ATTRIBUTE_A, valueB);
    }

    private JmxMetricsConfiguration.MBeanMetricsConfig buildMBeanConfig(String beanName, String metricName, JmxAttributeIdentifier... attributes) {
        return new JmxMetricsConfiguration.MBeanMetricsConfig(beanName, metricName, Set.of(attributes));
    }
}