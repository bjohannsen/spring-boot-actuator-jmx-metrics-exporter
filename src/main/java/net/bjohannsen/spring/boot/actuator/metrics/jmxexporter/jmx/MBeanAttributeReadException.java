package net.bjohannsen.spring.boot.actuator.metrics.jmxexporter.jmx;

/**
 * Exception for errors that occurred while reading MBean attribute values.
 */
public class MBeanAttributeReadException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message given message for the exception
     */
    public MBeanAttributeReadException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param message given message for the exception
     * @param cause cause of this exception
     */
    public MBeanAttributeReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
