package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx;

import java.util.List;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.parser.AttributeValueParser;

/**
 * Registry for {@link AttributeValueParser AttributeValueParsers}.
 */
public class AttributeParserRegistry {

    private final List<AttributeValueParser> parsers;

    /**
     * Constructor.
     *
     * @param parsers given attribute parsers
     */
    public AttributeParserRegistry(List<AttributeValueParser> parsers) {
        this.parsers = parsers;
    }

    /**
     * Find suitable parser for given attribute value.
     *
     * @param attribute given attribute
     * @return a suitable attribute parser
     * @throws MBeanAttributeReadException if can no suitable attribute parser is found
     */
    public AttributeValueParser findParserFor(Object attribute) {
        return parsers.stream()
                .filter(parser -> parser.canHandle(attribute))
                .findFirst()
                .orElseThrow(() ->
                        new MBeanAttributeReadException("No suitable JMX attribute parser found for attribute value [" + attribute + "]"));

    }
}
