package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.config.JmxAttributeIdentifier;
import net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx.parser.AttributeValueParser;

/**
 * Reader for JMX MBean attributes.
 */
public class MBeanAttributeReader {

    private final MBeanServer mBeanServer;
    private final AttributeParserRegistry parserRegistry;

    /**
     * Constructor.
     *
     * @param mBeanServer mbean server.
     * @param parserRegistry registry for {@link AttributeValueParser}s.
     */
    public MBeanAttributeReader(MBeanServer mBeanServer, AttributeParserRegistry parserRegistry) {
        this.mBeanServer = mBeanServer;
        this.parserRegistry = parserRegistry;
    }

    /**
     * Query a MBean via given name and extract the value of a specific attribute. The value has to be an instance of
     * {@link Number}, otherwise no value will be returned.
     *
     * @param mBeanName     the name of the mbean to look for.
     * @param attributeId attribute to fetch the value from
     * @return the obtained attribute value or <code>Optional.empty</code> if an error occurred.
     * @throws MBeanAttributeReadException if the attribute could not be read.
     */
    public double findMBeanAttributeValue(String mBeanName, JmxAttributeIdentifier attributeId) {
        Object attributeValue = findMBeanAttribute(mBeanName, attributeId);
        AttributeValueParser parser = parserRegistry.findParserFor(attributeValue);
        return parser.parseNumericValue(attributeValue, attributeId);
    }

    private Object findMBeanAttribute(String mBeanName, JmxAttributeIdentifier attributeId) {
        try {
            ObjectName mBeanObjectName = ObjectName.getInstance(mBeanName);
            return mBeanServer.getAttribute(mBeanObjectName, attributeId.getName());
        } catch (MalformedObjectNameException e) {
            throw new MBeanAttributeReadException("Invalid MBean name [" + mBeanName + "].", e);
        } catch (InstanceNotFoundException e) {
            throw new MBeanAttributeReadException("No MBean found for name [" + mBeanName + "].", e);
        } catch (AttributeNotFoundException e) {
            throw new MBeanAttributeReadException("No Attribute [" + attributeId +"] found for MBean [" + mBeanName + "].", e);
        } catch (ReflectionException | MBeanException e) {
            throw new MBeanAttributeReadException("Error while scraping attribute [" + attributeId + "] of MBean [" + mBeanName + "].", e);
        }
    }
}
