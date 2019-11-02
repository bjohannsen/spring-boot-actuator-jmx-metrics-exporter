package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ValueReferenceStoreTest {

    private static final String KEY = "key";

    private final ValueReferenceStore referenceStore = new ValueReferenceStore();

    @Test
    public void thatKeepReferenceWorks() {
        // given
        Double value = 19.09d;

        // when
        Double result = referenceStore.keepReference(KEY, value);

        // then
        assertThat(referenceStore.getValue(KEY), is(value));
        assertThat(result, is(value));
    }
}