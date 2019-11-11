package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.parser;

import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxAttributeIdentifier;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReadException;

/**
 * Base class for attribute parser implementations.
 * Provides implementation to parse the given value to a double from instances of {@link Number} or {@link String}.
 */
public abstract class BaseAttributeValueParser implements AttributeValueParser {

    @Override
    public double parseNumericValue(Object attributeValue, JmxAttributeIdentifier attributeId) {
        if (attributeValue instanceof Number) {
            return ((Number) attributeValue).doubleValue();
        } else if (attributeValue instanceof String) {
            try {
                return Double.parseDouble((String) attributeValue);
            } catch (NumberFormatException e) {
                throw new MBeanAttributeReadException("Could not parse double from string value of [" + attributeId + "].", e);
            }
        }
        throw new MBeanAttributeReadException("Unsupported attribute type for [" + getClass().getSimpleName() + "]");
    }
}
