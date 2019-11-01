package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.verify;

public class MetricFacadeTest {

    private static final String METRIC_NAME = "metricName";

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private MeterRegistry meterRegistryMock;

    @Mock
    private ValueReferenceStore valueReferenceStoreMock;

    @InjectMocks
    private MetricFacade metricFacade;

    @Test
    public void thatSubmitGaugeStoresReferencesAndPassesValueToMicrometer() {
        // given
        Double value = 19.09;

        // when
        metricFacade.submitGauge(METRIC_NAME, value);

        // then
        verify(valueReferenceStoreMock).keepReference(METRIC_NAME, value);
        verify(meterRegistryMock).gauge(METRIC_NAME, value);
    }
}