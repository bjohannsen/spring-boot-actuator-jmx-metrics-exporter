package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Identifier for JMX attributes.
 * Attributes are identified by their name, but in case of CompositeType attributes, the key is needed to identify the
 * component.
 */
public class JmxAttributeIdentifier {

    public static final String DELIMITER = ".";
    private static final String REGEX_DELIMITER = "\\"+DELIMITER;

    /**
     * Unique name of the attribute
     */
    private final String name;

    /**
     * Key for composite attributes.
     */
    private final String key;

    @JsonCreator
    public static JmxAttributeIdentifier of(String attributeIdentifier) {
        String[] parts = attributeIdentifier.split(REGEX_DELIMITER,2);
        String key = parts.length > 1 ? parts[1] : null;
        return new JmxAttributeIdentifier(parts[0], key);
    }

    private JmxAttributeIdentifier(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        if (this.key != null) {
            return this.name + DELIMITER + this.key;
        }
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JmxAttributeIdentifier that = (JmxAttributeIdentifier) o;

        if (!name.equals(that.name)) return false;
        return key != null ? key.equals(that.key) : that.key == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }
}
