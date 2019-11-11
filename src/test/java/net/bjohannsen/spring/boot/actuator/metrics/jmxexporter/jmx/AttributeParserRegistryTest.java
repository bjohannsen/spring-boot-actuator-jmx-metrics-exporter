package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx;

import java.util.List;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.parser.AttributeValueParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class AttributeParserRegistryTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private AttributeValueParser attributeParserAMock;
    @Mock
    private AttributeValueParser attributeParserBMock;

    private AttributeParserRegistry registry;

    @Before
    public void setUp() {
        this.registry = new AttributeParserRegistry(List.of(attributeParserAMock, attributeParserBMock));
    }

    @Test
    public void thatRegistryReturnsParserThatCanHandleGivenAttribute() {
        // given
        Object attribute = "someAttribute";
        when(attributeParserAMock.canHandle(attribute)).thenReturn(false);
        when(attributeParserBMock.canHandle(attribute)).thenReturn(true);

        // when
        AttributeValueParser parser = registry.findParserFor(attribute);

        // then
        assertThat(parser, is(attributeParserBMock));
    }

    @Test(expected = MBeanAttributeReadException.class)
    public void thatRegistryThrowsExceptionIfNoMatchingParserIsFound() {
        // given
        Object attribute = "someAttribute";
        when(attributeParserAMock.canHandle(attribute)).thenReturn(false);
        when(attributeParserBMock.canHandle(attribute)).thenReturn(false);

        // when
       registry.findParserFor(attribute);

        // then -> exception
    }
}