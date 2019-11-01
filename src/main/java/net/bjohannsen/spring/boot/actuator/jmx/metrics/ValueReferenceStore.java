package net.bjohannsen.spring.boot.actuator.jmx.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Micrometer relies on weak references for gauges.
 * This class is used to hold and store references to values to save them from GC.
 */
class ValueReferenceStore {

    private final Map<String, Double> map = new ConcurrentHashMap<>();

    /**
     *
     * @param key
     * @param value
     * @return
     */
    double keepReference(String key, Double value) {
        map.put(key, value);
        return value;
    }

    /**
     *
     * @param key
     * @return
     */
    Double getValue(String key) {
        return map.get(key);
    }

}
