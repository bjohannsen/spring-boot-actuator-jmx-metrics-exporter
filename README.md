[![Build](https://gitlab.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/badges/master/pipeline.svg)](https://gitlab.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/badges/master/pipeline.svg)
[![Build](https://gitlab.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/badges/master/coverage.svg)](https://gitlab.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/badges/master/coverage.svg)
![Maven Central](https://img.shields.io/maven-central/v/net.bjohannsen/spring-boot-actuator-jmx-metrics-exporter)


# spring-boot-actuator-jmx-metrics-exporter
Glue Library to publish JMX MBean attributes as Spring Actuator metrics. 
Supports simple attribute values that can be parsed as a Number as well as composite attribute types.

It reads configured attributes from MBeans and submits them to the Spring Boot Actuator metrics system (i.e. Micrometer).
The JMX data is scraped in a configurable interval to avoid performance issues.

## How to use

Add dependency:
```
runtime 'net.bjohannsen:spring-boot-actuator-jmx-metrics-exporter:2.0.0-SNAPSHOT")
```

Requirements:
- Java 11 or higher
- Micrometer (Spring Boot2 Actuator or Spring Boot 1.5 with [Micrometer Legacy adapter](https://micrometer.io/docs/ref/spring/1.5))
- Jmx should be enabled in spring config ((spring.jmx.enabled=true). It is disabled by default since Spring Boot 2.2)

## Configuration

Configuration property prefix is 'jmx-metrics-export'.

### Properties

| Key              | Default                             | Description                                             |
|------------------|-------------------------------------|---------------------------------------------------------|
| enabled          | false                               | Enable metrics                                          |
| prefix           | jmx                                 | Prefix for exposed metrics                              |
| scrape-interval  | 10000                               | Interval to scrape data from MBeans in milliseconds     |
| config-files     | classpath:jmx-metrics-exporter.json | List of resource links to mbean config json files (see below) |

### jmx-metrics-exporter.json

```
{
  "prefix": "jmx",
  "mbeans": [
    {
      "mbeanName": "net.bjohannsen.spring.boot.actuator.metrics.jmxexporter:name=mBean,type=MBeanClass",
      "metricName": "myMetric",
      "attributes": ["SomeAttribute", "AnotherAttribute"]
    }
  ]
}
```

The example will expose metrics 'jmx.myMetric.SomeAttribute' and 'jmx.myMetric.AnotherAttribute'. 
Multiple config files can be specified and will be used in an additive manner.

#### Composite types

To export composite type members, simply config the attribute as 'attributeName.memberKey' format. Example:
```
...
  "mbeans": [
    {
      "mbeanName": "java.lang:type=Memory",
      "metricName": "memory",
      "attributes": ["HeapMemoryUsage.max"]
    }
  ]
...
```

### Examples

Example configurations for application.yml and config files can be found in the [application-test.yml](https://github.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/blob/master/src/test/resources/application-test.yml) 
and [mbean-metrics-config.json](https://github.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/blob/master/src/test/resources/mbean-metrics-config.json).
