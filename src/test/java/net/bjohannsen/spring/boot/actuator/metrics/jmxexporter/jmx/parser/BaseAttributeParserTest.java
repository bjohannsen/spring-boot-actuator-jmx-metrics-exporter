package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.parser;

import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReadException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class BaseAttributeParserTest {

    private final BaseAttributeValueParser parser = new BaseAttributeValueParser() {
        @Override
        public boolean canHandle(Object jmxAttributeType) {
            return false;
        }
    };

    @Test
    public void thatParserCanParserDouble() {
        // given
        double value = 19.09d;

        // when
        double result = parser.parseNumericValue(value, null);

        // then
        assertThat(result, equalTo(value));
    }

    @Test
    public void thatParserCanParserString() {
        // given
        String value = "19.09";
        double expectedResult = 19.09d;

        // when
        double result = parser.parseNumericValue(value, null);

        // then
        assertThat(result, equalTo(expectedResult));
    }

    @Test(expected = MBeanAttributeReadException.class)
    public void thatParserThrowsExceptionIfStringCanNotBeParsedAsNumber() {
        // given
        String value = "hurz";

        // when
        parser.parseNumericValue(value, null);

        // then -> exception
    }

    @Test(expected = MBeanAttributeReadException.class)
    public void thatParserThrowsExceptionIfValueIsNotStringOrNumber() {
        // given
        Object value = new Object();

        // when
        parser.parseNumericValue(value, null);

        // then -> exception
    }
}