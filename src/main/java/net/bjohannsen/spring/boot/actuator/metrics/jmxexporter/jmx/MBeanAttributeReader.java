package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx;

import java.util.Optional;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reader for JMX MBean attributes.
 */
public class MBeanAttributeReader {

    private static final Logger log = LoggerFactory.getLogger(MBeanAttributeReader.class);

    private final MBeanServer mBeanServer;

    /**
     * Constructor.
     *
     * @param mBeanServer mbean server.
     */
    public MBeanAttributeReader(MBeanServer mBeanServer) {
        this.mBeanServer = mBeanServer;
    }

    /**
     * Query a MBean via given name and extract the value of a specific attribute. The value has to be an instance of
     * {@link Number}, otherwise no value will be returned.
     *
     * @param mBeanName the name of the mbean to look for.
     * @param attributeName attribute to fetch the value from
     * @return the obtained attribute value or <code>Optional.empty</code> if an error occured.
     */
    public Optional<Double> findMBeanAttributeValue(String mBeanName, String attributeName) {
        try {
            ObjectName mBeanObjectName = ObjectName.getInstance(mBeanName);
            Number attributeValue = (Number) mBeanServer.getAttribute(mBeanObjectName, attributeName);
            return Optional.of(attributeValue.doubleValue());
        } catch (MalformedObjectNameException e) {
            log.info("Invalid MBean name [{}].", mBeanName, e);
        } catch (InstanceNotFoundException e) {
            log.info("No MBean found for name [{}].", mBeanName, e);
        } catch (AttributeNotFoundException e) {
            log.info("No Attribute [{}] found for MBean [{}].", attributeName, mBeanName, e);
        }  catch (ReflectionException | MBeanException e) {
            log.info("Error while scraping attribute [{}] of MBean [{}].", attributeName, mBeanName, e);
        }
        return Optional.empty();
    }
}
