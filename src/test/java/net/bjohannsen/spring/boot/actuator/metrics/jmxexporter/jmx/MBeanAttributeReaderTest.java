package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx;

import java.util.Optional;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MBeanAttributeReaderTest {

    private static final String M_BEAN_NAME = "net.bjohannsen:name=mBean,type=SomeBean";
    private static final String ATTRIBUTE_NAME = "attributeName";
    private static final Double VALUE = 19.09d;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private MBeanServer mBeanServerMock;

    @InjectMocks
    private MBeanAttributeReader attributeReader;

    @Test
    public void thatFindAttributeValueWorks() throws MalformedObjectNameException, AttributeNotFoundException, MBeanException,
            ReflectionException, InstanceNotFoundException {
        // given
        ObjectName objectName = ObjectName.getInstance(M_BEAN_NAME);
        when(mBeanServerMock.getAttribute(objectName, ATTRIBUTE_NAME)).thenReturn(VALUE);

        // when
        Optional<Double> result = attributeReader.findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_NAME);

        // then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), equalTo(VALUE));
        verify(mBeanServerMock).getAttribute(objectName, ATTRIBUTE_NAME);
    }

    @Test
    public void thatMalformedBeanNameDoesNotReturnAValueOrRaiseException() {
        // given & when
        Optional<Double> result = attributeReader.findMBeanAttributeValue("malformedBeanName", ATTRIBUTE_NAME);

        // then
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void thatNonExistingAttributeDoesNotReturnValueOrRaiseException() throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        // given
        ObjectName objectName = ObjectName.getInstance(M_BEAN_NAME);
        when(mBeanServerMock.getAttribute(objectName, ATTRIBUTE_NAME)).thenThrow(new AttributeNotFoundException());

        // when
        Optional<Double> result = attributeReader.findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_NAME);

        // then
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void thatNonExistingMBeanDoesNotReturnValueOrRaiseException() throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        // given
        ObjectName objectName = ObjectName.getInstance(M_BEAN_NAME);
        when(mBeanServerMock.getAttribute(objectName, ATTRIBUTE_NAME)).thenThrow(new InstanceNotFoundException());

        // when
        Optional<Double> result = attributeReader.findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_NAME);

        // then
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void thatOtherErrorsDoNotReturnValueOrRaiseException() throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        // given
        ObjectName objectName = ObjectName.getInstance(M_BEAN_NAME);
        when(mBeanServerMock.getAttribute(objectName, ATTRIBUTE_NAME)).thenThrow(new ReflectionException(new Exception()));

        // when
        Optional<Double> result = attributeReader.findMBeanAttributeValue(M_BEAN_NAME, ATTRIBUTE_NAME);

        // then
        assertThat(result.isPresent(), is(false));
    }
}