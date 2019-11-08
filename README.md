[![Build](https://gitlab.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/badges/master/pipeline.svg)](https://gitlab.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/badges/master/pipeline.svg)
[![Build](https://gitlab.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/badges/master/coverage.svg)](https://gitlab.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/badges/master/coverage.svg)
![Maven Central](https://img.shields.io/maven-central/v/net.bjohannsen/spring-boot-actuator-jmx-metrics-exporter)


# spring-boot-actuator-jmx-metrics-exporter
Glue Library to publish JMX MBean attributes as Spring Actuator metrics.

## How it works
It reads configured attributes from MBeans and submits them to the Spring Boot Actuator metrics system (i.e. Micrometer).
The JMX data is scraped in a configurable interval to avoid performance issues.

## How to use

Add dependency:
```
runtime 'net.bjohannsen:spring-boot-actuator-jmx-metrics-exporter:1.0.0-RELEASE")
```

Requirements:
- Java 11 or higher
- Micrometer (Spring Boot2 Actuator or Spring Boot 1.5 with [Micrometer Legacy adapter](https://micrometer.io/docs/ref/spring/1.5))
- Jmx should be enabled in spring config ((spring.jmx.enabled=true). It is disabled by default since Spring Boot 2.2)

### Configuration

Configuration properties are located under 'jmx-metrics-export'.

#### Global configuration

| Key              | Default                             | Description                                             |
|------------------|-------------------------------------|---------------------------------------------------------|
| enabled          | false                               | Enable metrics                                          | 
| scrape-interval  | 10000                               | Interval to scrape data from MBeans in milliseconds     |
| config-file      | classpath:jmx-metrics-exporter.json | Resource link to mbean config json file (see below)     |

#### jmx-metrics-exporter.json

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

Example configurations for application.yml and config files can be found in the [application-test.yml](https://github.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/blob/master/src/test/resources/application-test.yml) 
and [jmx-metrics-exporter.json](https://github.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/blob/master/src/test/resources/jmx-metrics-exporter.json).
