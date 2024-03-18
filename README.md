<!-- First line should be an H1: Badges on top please! -->
<!-- markdownlint-disable first-line-heading/first-line-h1 -->
[![Actions](https://github.com/Hapag-Lloyd/log4j2-stacktrace-filter-plugin/workflows/Release/badge.svg)](https://github.com/Hapag-Lloyd/dist-comm-vis-api/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.hlag.logging/log4j2-stacktrace-filter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.hlag.tools.commvis/api)
<!-- markdownlint-enable first-line-heading/first-line-h1 -->

# Filter Stacktrace Plugin for Log4j2

This project contains a stacktrace filter for the Log4j2 logging system. It can be included in a `JsonTemplateLayout`.

The plugin reduces the stacktrace by filtering classes which are not of any interest. It has a built-in list of packages to exclude
from the stacktrace.

# Output

Adding the filter to the logging configuration with `<EventTemplateAdditionalField key="error" format="JSON" value='{"$resolver": "filteredStacktraceException"}'/>`
produces the following Json object in the log stream:

<!-- no line breaks in Json please -->
<!-- markdownlint-disable line-length -->
```json
"error": {
  "stack": "java.lang.RuntimeException: java.lang.ArithmeticException: / by zero\r\n\tat com.hlag.logging.log4j2.LoggerIntegrationTest.wrapException(LoggerIntegrationTest.java:21)\r\n\tat com.hlag.logging.log4j2.LoggerIntegrationTest.logException(LoggerIntegrationTest.java:11)\r\n\t[suppressed 70 lines]\r\ncaused by java.lang.ArithmeticException\r\n\tat com.hlag.logging.log4j2.LoggerIntegrationTest.throwArithmeticException(LoggerIntegrationTest.java:25)\r\n\tat com.hlag.logging.log4j2.LoggerIntegrationTest.wrapException(LoggerIntegrationTest.java:19)\r\n\tat com.hlag.logging.log4j2.LoggerIntegrationTest.logException(LoggerIntegrationTest.java:11)\r\n\t[suppressed 70 lines]",
  "name": "java.lang.RuntimeException",
  "message": "java.lang.ArithmeticException: / by zero",
  "totalFilteredElements": 140
}
```
<!-- markdownlint-enable line-length -->

140 elements have been removed from the stacktrace. Only those remain which are relevant for your application.

# Usage

## Maven Dependency

Include the following artifact to your project which contains the `log4j2.xml` configuration file. Make sure to use the newest version.

```xml
<dependency>
    <groupId>com.hlag.logging</groupId>
    <artifactId>log4j2-stacktrace-filter</artifactId>
    <!-- make sure to use the newest version -->
    <version>1.0.0</version>
</dependency>
```

## Log4j2 Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="INFO">

    <Appenders>
        <Console name="console-datadog" target="SYSTEM_OUT">
            <JsonTemplateLayout eventTemplateUri="classpath:JsonLayout.json" maxStringLength="32768"  stackTraceEnabled="false">
                <EventTemplateAdditionalField key="error" format="JSON" value='{"$resolver": "filteredStacktraceException"}'/>
            </JsonTemplateLayout>
        </Console>
    </Appenders>

    <Loggers>
      <Root level="info">
          <appender-ref ref="console" />
      </Root>
    </Loggers>
</Configuration>
```
