package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxAttributeIdentifier;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReadException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CompositeTypeAttributeParserTest {

    private CompositeTypeAttributeParser parser = new CompositeTypeAttributeParser();

    @Test
    public void thatParserCanHandleCompositeData() {
        // given & when
        boolean result = parser.canHandle(new TestCompositeData());

        // then
        assertThat(result, is(true));
    }

    @Test
    public void thatParserCanNotHandleDoubles() {
        // given & when
        boolean result = parser.canHandle(19.09d);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void thatParserCanNotHandleStringValues() {
        // given & when
        boolean result = parser.canHandle("19.09");

        // then
        assertThat(result, is(false));
    }

    @Test
    public void thatParserCanExtractMembersFromCompositeData() {
        // given
        Map<String, Object> testValues = new HashMap<>();
        testValues.put("member", 19.09d);
        JmxAttributeIdentifier attributeIdentifier = JmxAttributeIdentifier.of("attribute.member");
        CompositeData compositeData = new TestCompositeData(testValues);

        // when
        double value = parser.parseNumericValue(compositeData, attributeIdentifier);

        // then
        assertThat(value, equalTo(19.09d));
    }

    @Test(expected = MBeanAttributeReadException.class)
    public void thatParserThrowsExceptionOnNonExistingMember() {
        // given
        Map<String, Object> testValues = new HashMap<>();
        testValues.put("member", 19.09d);
        JmxAttributeIdentifier attributeIdentifier = JmxAttributeIdentifier.of("attribute.nonExistingMember");
        CompositeData compositeData = new TestCompositeData(testValues);

        // when
        parser.parseNumericValue(compositeData, attributeIdentifier);

        // then -> exception
    }

    static class TestCompositeData implements CompositeData {

        private final Map<String, Object> map = new HashMap<>();

        TestCompositeData() {
        }

        TestCompositeData(Map<String, Object> values) {
            this.map.putAll(values);
        }

        @Override
        public CompositeType getCompositeType() {
            return null;
        }

        @Override
        public Object get(String key) {
            return map.get(key);
        }

        @Override
        public Object[] getAll(String[] keys) {
            return new Object[0];
        }

        @Override
        public boolean containsKey(String key) {
            return map.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Collection<?> values() {
            return null;
        }
    }
}