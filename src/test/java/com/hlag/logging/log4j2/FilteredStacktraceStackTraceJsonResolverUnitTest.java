package com.hlag.logging.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverContext;
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class FilteredStacktraceStackTraceJsonResolverUnitTest {
    private FilteredStacktraceExceptionResolver filteredStacktraceExceptionResolver;

    private JsonWriter jsonWriter;

    @Mock
    private EventResolverContext mockedEventResolverContext;

    @BeforeEach
    void setUp() {
        Mockito.when(mockedEventResolverContext.getRecyclerFactory()).thenReturn(new QueueingRecyclerFactory(LinkedList::new));
        Mockito.when(mockedEventResolverContext.getMaxStringByteCount()).thenReturn(60000);

        jsonWriter = JsonWriter.newBuilder().setMaxStringLength(60000).setTruncatedStringSuffix("...").build();

        FilteredStacktraceExceptionResolver.setPackagesToRemoveFromStacktrace(Arrays.asList("com.hlag.logging.log4j2"));
        filteredStacktraceExceptionResolver = new FilteredStacktraceExceptionResolver(mockedEventResolverContext, null);
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
    void shouldHaveTotalFilteredElementsAttribute_whenResolve() {
        LogEvent givenLogEvent = TestLogEvent.builder().thrown(createExceptionWithStacktrace()).build();

        filteredStacktraceExceptionResolver.resolve(givenLogEvent, jsonWriter);
        String actualStringOutput = jsonWriter.getStringBuilder().toString();

        JSONObject actualLogOutput = new JSONObject(actualStringOutput);

        Assertions.assertThat(actualLogOutput.get("totalFilteredElements")).isEqualTo(2);
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
                .filteredOn(line -> line.equals("caused by java.lang.ArithmeticException"))
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
                .filteredOn(line -> !line.startsWith("caused by"))
                .filteredOn(line -> !line.startsWith("java.lang.RuntimeException"))
                .allMatch(line -> line.startsWith("\tat "));
    }

    private Throwable wrappedThrowable() {
        return new RuntimeException(createExceptionWithStacktrace());
    }

    private Exception createExceptionWithStacktrace() {
        try {
            int a = 0 / 0;
        } catch (Exception e) {
            return e;
        }

        // never reached
        return new RuntimeException();
    }
}