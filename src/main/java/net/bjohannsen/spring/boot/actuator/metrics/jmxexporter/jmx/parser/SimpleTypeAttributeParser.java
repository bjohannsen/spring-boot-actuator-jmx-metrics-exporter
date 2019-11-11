package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.parser;

/**
 * Attribute reader to cover all simple attribute types that can casted to a numeric. So all instances of {@link Number}
 * and Strings as well.
 */
public class SimpleTypeAttributeParser extends BaseAttributeValueParser {

    @Override
    public boolean canHandle(Object attributeType) {
        return attributeType instanceof Number || attributeType instanceof String;
    }
}
