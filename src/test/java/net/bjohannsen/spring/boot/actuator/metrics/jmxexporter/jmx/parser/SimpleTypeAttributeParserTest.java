package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.parser;

import java.math.BigDecimal;
import java.util.Collection;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SimpleTypeAttributeParserTest {

    private final SimpleTypeAttributeParser parser = new SimpleTypeAttributeParser();

    @Test
    public void thatNumericTypeParserCanHandleDoubleValues() {
        // given & when
        boolean result = parser.canHandle(19.09d);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void thatNumericTypeParserCanHandleLongValues() {
        // given & when
        boolean result = parser.canHandle(1909L);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void thatNumericTypeParserCanHandleIntValues() {
        // given & when
        boolean result = parser.canHandle(1909);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void thatNumericTypeParserCanHandleStringValues() {
        // given & when
        boolean result = parser.canHandle("1909");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void thatNumericTypeParserCanHandleBigDecimals() {
        // given & when
        boolean result = parser.canHandle(BigDecimal.valueOf(19.09d));

        // then
        assertThat(result, is(true));
    }

    @Test
    public void thatNumericTypeParserCanNotHandleCompositeTypes() {
        // given & when
        boolean result = parser.canHandle(new CompositeData() {
            @Override
            public CompositeType getCompositeType() {
                return null;
            }

            @Override
            public Object get(String key) {
                return null;
            }

            @Override
            public Object[] getAll(String[] keys) {
                return new Object[0];
            }

            @Override
            public boolean containsKey(String key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public Collection<?> values() {
                return null;
            }
        });

        // then
        assertThat(result, is(false));
    }
}