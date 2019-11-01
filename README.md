# spring-boot-actuator-jmx-metrics-exporter
Library to publish JMX MBean attributes as Spring Actuator metrics

For performance reasons, the MBeans are only queried in a scrape interval of 10s.

ToDo:
- make scrape interval configurable
- tests
- example application 