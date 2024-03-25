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

import java.util.Collections;
import java.util.LinkedList;

@ExtendWith(MockitoExtension.class)
class FilteredStacktraceStackTraceJsonResolverWhitelistUnitTest {
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

        // sets the package whitelist
        Mockito.when(mockedConfig.getList("whitelistPackages", String.class)).thenReturn(Collections.singletonList("com.hlag.logging.log4j2"));

        jsonWriter = JsonWriter.newBuilder().setMaxStringLength(60000).setTruncatedStringSuffix("...").build();

        filteredStacktraceExceptionResolver = new FilteredStacktraceExceptionResolver(mockedEventResolverContext, mockedConfig);
    }

    @Test
    void shouldHaveWhitelistedPackagesOnly_whenResolve_givenThrowable() {
        LogEvent givenLogEvent = TestLogEvent.builder().thrown(wrappedThrowable()).build();

        filteredStacktraceExceptionResolver.resolve(givenLogEvent, jsonWriter);
        String actualStringOutput = jsonWriter.getStringBuilder().toString();

        JSONObject actualLogOutput = new JSONObject(actualStringOutput);

        Assertions.assertThat(actualLogOutput.get("stack").toString().split(System.lineSeparator()))
                .filteredOn(line -> line.startsWith("\tat "))
                .allSatisfy(line -> Assertions.assertThat(line).startsWith("\tat com.hlag.logging.log4j2."));
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
