# spring-boot-actuator-jmx-metrics-exporter
Library to publish JMX MBean attributes as Spring Actuator metrics

For performance reasons, the JMX data is only scraped in a fixed interval of 10s.

## How to use

Add dependency:
```
runtime 'net.bjohannsen:spring-boot-actuator-jmx-metrics-exporter:0.1.0")
```
Make sure that you have JMX enabled in your spring configuration (spring.jmx.enabled=true). It is disabled by default since Spring Boot 2.2.0

## Configuration

Configuration properties are located under 'jmx-metrics-export'.

### Global configuration

| Key              | Default    | Description                                             |
|------------------|------------|---------------------------------------------------------|
| enabled          | false      | Enable metrics                                          | 
| scrape-interval  | 10000      | Interval to scrape data from MBeans in milliseconds     |
| prefix           | jmx        | Prefix applied to all exposed metrics                   |
| mbeans           | empty list | List of Mbeans to scrape metrics from (see table below) |

### MBean attribute configuration 

| Key              | Description                                     | Example                |
|------------------|-------------------------------------------------|------------------------|
| name             | JMX object name of the MBean                    |                        | 
| metrics-name     | Name of the metric for the MBean                | myBean                 |
| attributes       | List of attributes to scrape from the intervals | Attribute1, Attribute2 |

An example configuration can be found in the [application-test.yml](https://github.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/blob/master/src/test/resources/application-test.yml)
