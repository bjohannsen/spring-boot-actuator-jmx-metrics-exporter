package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.parser;

import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxAttributeIdentifier;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReadException;

/**
 * Interface to parse values from JMX MBean attributes.
 */
public interface AttributeValueParser {

    /**
     * Check if the parser if compatible with the given attribute type.
     *
     * @param jmxAttributeType given attribute type
     * @return <code>true</code> if parser can handle the attribute type
     */
    boolean canHandle(Object jmxAttributeType);

    /**
     * Extract the numeric value of the attribute.
     *
     * @param attributeValue given attribute value
     * @param attributeId given attribute id
     * @return the extracted value
     * @throws MBeanAttributeReadException in case of an error
     */
    double parseNumericValue(Object attributeValue, JmxAttributeIdentifier attributeId);
}
