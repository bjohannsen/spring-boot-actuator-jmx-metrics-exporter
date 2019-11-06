# spring-boot-actuator-jmx-metrics-exporter
Library to publish JMX MBean attributes as Spring Actuator metrics

For performance reasons, the JMX data is only scraped in an interval of 10s.

## How to use

Add dependency:
```
runtime 'net.bjohannsen:spring-boot-actuator-jmx-metrics-exporter:0.1.0")
```
Make sure that you have JMX enabled in your spring configuration (spring.jmx.enabled=true). It is disabled by default since Spring Boot 2.2.0

## Configuration

Configuration properties are located under 'jmx-metrics-export'.

### Global configuration

| Key              | Default                             | Description                                             |
|------------------|-------------------------------------|---------------------------------------------------------|
| enabled          | false                               | Enable metrics                                          | 
| scrape-interval  | 10000                               | Interval to scrape data from MBeans in milliseconds     |
| config-file      | classpath:jmx-metrics-exporter.json | Resource link to mbean config json file (see below)     |

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

Example configurations for application.yml and config files can be found in the [application-test.yml](https://github.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/blob/master/src/test/resources/application-test.yml) 
and [jmx-metrics-exporter.json](https://github.com/bjohannsen/spring-boot-actuator-jmx-metrics-exporter/blob/master/src/test/resources/jmx-metrics-exporter.json).
