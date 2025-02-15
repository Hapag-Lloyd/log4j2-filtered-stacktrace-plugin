package com.hlag.logging.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverContext;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolverConfig;
import org.apache.logging.log4j.layout.template.json.util.JsonWriter;
import org.apache.logging.log4j.layout.template.json.util.QueueingRecyclerFactory;
import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

@ExtendWith(MockitoExtension.class)
class FilteredStacktraceStackTraceJsonResolverUnitTest {
    private FilteredStacktraceExceptionResolver filteredStacktraceExceptionResolver;

    private JsonWriter jsonWriter;

    @Mock
    private EventResolverContext mockedEventResolverContext;
    @Mock
    private TemplateResolverConfig mockedConfig;

    @BeforeEach
    void setUp() {
        Mockito.when(mockedEventResolverContext.getRecyclerFactory()).thenReturn(new QueueingRecyclerFactory(LinkedList::new));
        Mockito.when(mockedEventResolverContext.getMaxStringByteCount()).thenReturn(60000);

        // this ensures that the internal mechanism of adding packages is working
        Mockito.when(mockedConfig.getList("additionalPackagesToIgnore", String.class)).thenReturn(Collections.singletonList("com.hlag.logging.log4j2"));
        Mockito.when(mockedConfig.getList("whitelistPackages", String.class)).thenReturn(new ArrayList<>());

        jsonWriter = JsonWriter.newBuilder().setMaxStringLength(60000).setTruncatedStringSuffix("...").build();

        filteredStacktraceExceptionResolver = new FilteredStacktraceExceptionResolver(mockedEventResolverContext, mockedConfig);
    }

    @Test
    void shouldHaveNameAttribute_whenResolve() {
        LogEvent givenLogEvent = TestLogEvent.builder().thrown(createExceptionWithStacktrace()).build();

        filteredStacktraceExceptionResolver.resolve(givenLogEvent, jsonWriter);
        String actualStringOutput = jsonWriter.getStringBuilder().toString();

        JSONObject actualLogOutput = new JSONObject(actualStringOutput);

        Assertions.assertThat(actualLogOutput.get("name")).isEqualTo("java.lang.ArithmeticException");
    }

    @Test
    void shouldHaveMessageAttribute_whenResolve() {
        LogEvent givenLogEvent = TestLogEvent.builder().thrown(createExceptionWithStacktrace()).build();

        filteredStacktraceExceptionResolver.resolve(givenLogEvent, jsonWriter);
        String actualStringOutput = jsonWriter.getStringBuilder().toString();

        JSONObject actualLogOutput = new JSONObject(actualStringOutput);

        Assertions.assertThat(actualLogOutput.get("message")).isEqualTo("/ by zero");
    }

    @Test
    void shouldHaveCorrectNumberOfCurlyBraces_whenResolve() {
        LogEvent givenLogEvent = TestLogEvent.builder().thrown(createExceptionWithStacktrace()).build();

        filteredStacktraceExceptionResolver.resolve(givenLogEvent, jsonWriter);
        String actualStringOutput = jsonWriter.getStringBuilder().toString();

        Assertions.assertThat(actualStringOutput).containsOnlyOnce("{");
        Assertions.assertThat(actualStringOutput).containsOnlyOnce("}");
    }

    @Test
    void shouldHaveTotalFilteredElementsAttribute_whenResolve() {
        LogEvent givenLogEvent = TestLogEvent.builder().thrown(createExceptionWithStacktrace()).build();

        filteredStacktraceExceptionResolver.resolve(givenLogEvent, jsonWriter);
        String actualStringOutput = jsonWriter.getStringBuilder().toString();

        JSONObject actualLogOutput = new JSONObject(actualStringOutput);

        // the exact number of lines is not important and depends on the Java version used
        Assertions.assertThat(actualLogOutput.get("totalFilteredElements")).isNotEqualTo(0);
    }

    @Test
    void shouldHaveExtendedStacktraceAttribute_whenResolve() {
        LogEvent givenLogEvent = TestLogEvent.builder().thrown(createExceptionWithStacktrace()).build();

        filteredStacktraceExceptionResolver.resolve(givenLogEvent, jsonWriter);
        String actualStringOutput = jsonWriter.getStringBuilder().toString();

        JSONObject actualLogOutput = new JSONObject(actualStringOutput);

        // there are other test cases which make sure that the stacktrace is correct
        Assertions.assertThat(actualLogOutput.get("stack")).isNotNull();
    }

    @Test
    void shouldListOneCausedBy_whenResolve_givenThrowableWithOneCausedBy() {
        LogEvent givenLogEvent = TestLogEvent.builder().thrown(wrappedThrowable()).build();

        filteredStacktraceExceptionResolver.resolve(givenLogEvent, jsonWriter);
        String actualStringOutput = jsonWriter.getStringBuilder().toString();

        JSONObject actualLogOutput = new JSONObject(actualStringOutput);

        Assertions.assertThat(actualLogOutput.get("stack").toString().split(System.lineSeparator()))
                .filteredOn(line -> line.equals("Caused by java.lang.ArithmeticException: / by zero"))
                .hasSize(1);
    }

    @Test
    void shouldHaveSuppressedLinesInExceptionAndCausedBy_whenResolve_givenThrowableWithOurPackage() {
        LogEvent givenLogEvent = TestLogEvent.builder().thrown(wrappedThrowable()).build();

        filteredStacktraceExceptionResolver.resolve(givenLogEvent, jsonWriter);
        String actualStringOutput = jsonWriter.getStringBuilder().toString();

        JSONObject actualLogOutput = new JSONObject(actualStringOutput);

        Assertions.assertThat(actualLogOutput.get("stack").toString().split(System.lineSeparator()))
                .filteredOn(line -> line.contains("[suppressed "))
                .hasSize(2);
    }

    @Test
    void shouldStartWithTabAndAtAndSpace_whenResolve_givenAnyStacktrace() {
        LogEvent givenLogEvent = TestLogEvent.builder().thrown(wrappedThrowable()).build();

        filteredStacktraceExceptionResolver.resolve(givenLogEvent, jsonWriter);
        String actualStringOutput = jsonWriter.getStringBuilder().toString();

        JSONObject actualLogOutput = new JSONObject(actualStringOutput);

        // trying to ignore all non stacktrace lines
        Assertions.assertThat(actualLogOutput.get("stack").toString().split(System.lineSeparator()))
                .filteredOn(line -> !line.startsWith("\t[suppressed"))
                .filteredOn(line -> !line.startsWith("Caused by"))
                .filteredOn(line -> !line.startsWith("java.lang.RuntimeException"))
                .allMatch(line -> line.startsWith("\tat "));
    }

    private Throwable wrappedThrowable() {
        return new RuntimeException(createExceptionWithStacktrace());
    }

    private Exception createExceptionWithStacktrace() {
        try {
            throw new ArithmeticException("/ by zero");
        } catch (Exception e) {
            return e;
        }
    }
}
