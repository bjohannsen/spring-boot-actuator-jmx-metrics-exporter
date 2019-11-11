package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxAttributeIdentifier;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.parser.AttributeValueParser;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MBeanAttributeReaderTest {

    private static final String M_BEAN_NAME = "net.bjohannsen:name=mBean,type=SomeBean";
    private static final JmxAttributeIdentifier ATTRIBUTE_ID = JmxAttributeIdentifier.of("attributeName");
    private static final Double VALUE = 19.09d;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private MBeanServer mBeanServerMock;
    @Mock
    private AttributeParserRegistry parserRegistryMock;
    @Mock
    private AttributeValueParser attributeParserMock;

    @InjectMocks
    private MBeanAttributeReader attributeReader;

    @Test
    public void thatFindAttributeValueWorks() throws MalformedObjectNameException, AttributeNotFoundException, MBeanException,
            ReflectionException, InstanceNotFoundException {
        // given
        ObjectName objectName = ObjectName.getInstance(M_BEAN_NAME);
        when(mBeanServerMock.getAttribute(objectName, ATTRIBUTE_ID.getName())).thenReturn(VALUE);
        when(parserRegistryMock.findParserFor(VALUE)).thenReturn(attributeParserMock);
        when(attributeParserMock.parseNumericValue(VALUE, ATTRIBUTE_ID)).thenReturn(VALUE);

        // when
       double result = attributeReader.findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_ID);

        // then
        assertThat(result, equalTo(VALUE));
        verify(mBeanServerMock).getAttribute(objectName, ATTRIBUTE_ID.getName());
    }

    @Test(expected = MBeanAttributeReadException.class)
    public void thatMalformedBeanNameDoesNotReturnAValueOrRaiseException() {
        // given & when
        attributeReader.findMBeanAttributeValue("malformedBeanName", ATTRIBUTE_ID);

        // then -> exception
    }

    @Test(expected = MBeanAttributeReadException.class)
    public void thatNonExistingAttributeDoesNotReturnValueOrRaiseException() throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        // given
        ObjectName objectName = ObjectName.getInstance(M_BEAN_NAME);
        when(mBeanServerMock.getAttribute(objectName, ATTRIBUTE_ID.getName())).thenThrow(new AttributeNotFoundException());

        // when
        attributeReader.findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_ID);

        // then -> exception
    }

    @Test(expected = MBeanAttributeReadException.class)
    public void thatNonExistingMBeanDoesNotReturnValueOrRaiseException() throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        // given
        ObjectName objectName = ObjectName.getInstance(M_BEAN_NAME);
        when(mBeanServerMock.getAttribute(objectName, ATTRIBUTE_ID.getName())).thenThrow(new InstanceNotFoundException());

        // when
        attributeReader.findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_ID);

        // then -> exception
    }

    @Test(expected = MBeanAttributeReadException.class)
    public void thatOtherErrorsAreWrappedAsMBeanAttributeReadingException() throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        // given
        ObjectName objectName = ObjectName.getInstance(M_BEAN_NAME);
        when(mBeanServerMock.getAttribute(objectName, ATTRIBUTE_ID.getName())).thenThrow(new ReflectionException(new Exception()));

        // when
       attributeReader.findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_ID);

        // then -> exception
    }
}