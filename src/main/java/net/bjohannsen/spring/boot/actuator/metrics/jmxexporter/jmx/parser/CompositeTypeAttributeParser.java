package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.parser;

import javax.management.openmbean.CompositeData;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxAttributeIdentifier;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.MBeanAttributeReadException;

/**
 * Attribute reader to handle all JMX CompositeType attributes.
 */
public class CompositeTypeAttributeParser extends BaseAttributeValueParser {

    @Override
    public boolean canHandle(Object attributeType) {
        return attributeType instanceof CompositeData;
    }

    @Override
    public double parseNumericValue(Object attributeData, JmxAttributeIdentifier attributeId) {
        CompositeData data = (CompositeData) attributeData;

        if (data.containsKey(attributeId.getKey())) {
            Object o = data.get(attributeId.getKey());
            return super.parseNumericValue(o, null);
        }
        throw new MBeanAttributeReadException("Member [" + attributeId.getKey() + "] does not exist for attribute [" + attributeId.getName() + "]");
    }

}
