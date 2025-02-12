<!-- First line should be an H1: Badges on top please! -->
<!-- markdownlint-disable first-line-h1 -->
[![Actions](https://github.com/Hapag-Lloyd/log4j2-filtered-stacktrace-plugin/workflows/Release/badge.svg)](https://github.com/Hapag-Lloyd/log4j2-filtered-stacktrace-plugin/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.hlag.logging/log4j2-stacktrace-filter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.hlag.logging/log4j2-stacktrace-filter-plugin)
<!-- markdownlint-enable first-line-h1 -->

# Filter Stacktrace Plugin for Log4j2

This project contains a stacktrace filter for the Log4j2 logging system. It can be included in a [`JsonTemplateLayout`](https://logging.apache.org/log4j/2.x/manual/json-template-layout.html).

The plugin reduces the stacktrace by filtering classes which are not of any interest. It has a built-in list of packages to exclude
from the stacktrace. Additional packages can be configured in the `log4j2.xml` file. Find the full blown example below.

This plugin was built on top of the work of the Apache Log4j team and takanuva15, who described the plugin in a
[Stackoverflow article](https://stackoverflow.com/questions/70614495/is-there-a-way-to-override-the-exceptionresolver-of-jsontemplatelayout-of-log4j2/77143208#77143208).
We replaced the main logic completely.

# Output

Adding the filter to the logging configuration with `<EventTemplateAdditionalField key="error" format="JSON" value='{"$resolver": "filteredStacktraceException"}'/>`
produces the following Json object in the log stream:

<!-- no line breaks in Json please, we are fine with long lines here -->
<!-- markdownlint-disable line-length -->
```json
"error": {
  "stack": "java.lang.RuntimeException: java.lang.ArithmeticException: / by zero\r\n\tat com.hlag.logging.log4j2.LoggerIntegrationTest.wrapException(LoggerIntegrationTest.java:21)\r\n\tat com.hlag.logging.log4j2.LoggerIntegrationTest.logException(LoggerIntegrationTest.java:11)\r\n\t[suppressed 70 lines]\r\nCaused by java.lang.ArithmeticException: / by zero\r\n\tat com.hlag.logging.log4j2.LoggerIntegrationTest.throwArithmeticException(LoggerIntegrationTest.java:25)\r\n\tat com.hlag.logging.log4j2.LoggerIntegrationTest.wrapException(LoggerIntegrationTest.java:19)\r\n\tat com.hlag.logging.log4j2.LoggerIntegrationTest.logException(LoggerIntegrationTest.java:11)\r\n\t[suppressed 70 lines]",
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
<!-- pom.xml -->
<dependency>
    <groupId>com.hlag.logging</groupId>
    <artifactId>log4j2-stacktrace-filter</artifactId>
    <!-- make sure to use the newest version -->
    <version>1.3.0</version>
</dependency>
```

## Log4j2 Configuration

<!-- no line breaks in Json please, we are fine with long lines here -->
<!-- markdownlint-disable line-length -->
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- log4j2.xml -->
<Configuration status="INFO">

    <Appenders>
        <Console name="console" target="SYSTEM_OUT">
            <JsonTemplateLayout eventTemplateUri="classpath:JsonLayout.json" maxStringLength="32768"  stackTraceEnabled="false">
                <EventTemplateAdditionalField key="error" format="JSON"
                                              value='{"$resolver": "filteredStacktraceException"}, "additionalPackagesToIgnore": ["com.hlag.logging.log4j2."]}' />
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
<!-- markdownlint-enable line-length -->

### additionalPackagesToIgnore

Use this parameter to add other packages to the built-in list. These packages are ignored too. Especially useful if you have to
include company internal frameworks, but you don't want to see them in the stacktrace.

Makes no sense to specify `whitelistedPackages` in addition to this parameter.

### whitelistPackages

Use this parameter to define packages which should remain when filtering a stack trace. Every other package is removed from the
stacktrace. The built-in list of packages to filter as well as the `additionalPackagesToIgnore` parameter have no effect when
`whitelistPackages` exists.
