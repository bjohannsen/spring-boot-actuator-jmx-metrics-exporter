package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Micrometer relies on weak references for gauges.
 * This class is used to hold and store references to values to save them from GC.
 *
 * Micrometer will only read from this reference store.
 */
public class ValueReferenceStore {

    private final Map<String, Double> map = new ConcurrentHashMap<>();

    /**
     * Stores the passed object to keep the reference alive.
     *
     * @param key given key
     * @param value value to store
     * @return the value
     */
    public Double keepReference(String key, Double value) {
        map.put(key, value);
        return value;
    }

    /**
     * Returns the stored value for a given key.
     *
     * @param key given key
     * @return the value
     */
    public Double getValue(String key) {
        return map.get(key);
    }
}
