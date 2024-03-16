package com.hlag.logging.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverContext;
import org.apache.logging.log4j.layout.template.json.util.JsonWriter;
import org.apache.logging.log4j.layout.template.json.util.QueueingRecyclerFactory;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class FilteredStacktraceExceptionResolverUnitTest {
    public static final List<String> PACKAGES_TO_REMOVE_FROM_STACKTRACE = List.of("org.junit");
    private FilteredStacktraceExceptionResolver filteredStacktraceExceptionResolver;

    @Mock
    private EventResolverContext mockedEventResolverContext;

    private JsonWriter jsonWriter;

    @BeforeEach
    void setUp() {
        // lenient() needed to build the filteredStacktraceExceptionResolver
        Mockito.lenient().when(mockedEventResolverContext.getRecyclerFactory()).thenReturn(new QueueingRecyclerFactory(LinkedList::new));
        Mockito.lenient().when(mockedEventResolverContext.getMaxStringByteCount()).thenReturn(10000);

        jsonWriter = JsonWriter.newBuilder().setMaxStringLength(10000).setTruncatedStringSuffix("...").build();

        FilteredStacktraceExceptionResolver.setPackagesToRemoveFromStacktrace(PACKAGES_TO_REMOVE_FROM_STACKTRACE);
        filteredStacktraceExceptionResolver = new FilteredStacktraceExceptionResolver(mockedEventResolverContext, null);
    }

    @Test
    void shouldReturnTrue_whenIsResolve_givenExceptionInLogEvent() {
        LogEvent givenLogEvent = TestLogEvent.builder().thrown(new RuntimeException()).build();

        Assertions.assertThat(filteredStacktraceExceptionResolver.isResolvable(givenLogEvent)).isTrue();
    }

    @Test
    void shouldReturnFalse_whenIsResolve_givenNoExceptionInLogEvent() {
        LogEvent givenLogEvent = TestLogEvent.builder().build();

        Assertions.assertThat(filteredStacktraceExceptionResolver.isResolvable(givenLogEvent)).isFalse();
    }

    @Test
    void shouldResolveToNull_whenResolve_givenNoExceptionInLogEvent() {
        LogEvent givenLogEvent = TestLogEvent.builder().build();

        filteredStacktraceExceptionResolver.resolve(givenLogEvent, jsonWriter);
        String actualOutput = jsonWriter.getStringBuilder().toString();

        Assertions.assertThat(actualOutput).isEqualTo("null");
    }

    @Test
    void shouldResolveToAJsonObject_whenResolve_givenExceptionInLogEvent() {
        LogEvent givenLogEvent = TestLogEvent.builder().thrown(createExceptionWithStacktrace()).build();

        filteredStacktraceExceptionResolver.resolve(givenLogEvent, jsonWriter);
        String actualStringOutput = jsonWriter.getStringBuilder().toString();

        JSONException actualException = Assertions.catchThrowableOfType(() -> new JSONObject(actualStringOutput), JSONException.class);

        Assertions.assertThat(actualException).isNull();
    }

    @Test
    void shouldRemoveSomePackagesFromStacktrace_whenResolve_givenStacktrace() {
        LogEvent givenLogEvent = TestLogEvent.builder().thrown(createExceptionWithStacktrace()).build();

        filteredStacktraceExceptionResolver.resolve(givenLogEvent, jsonWriter);
        String actualStringOutput = jsonWriter.getStringBuilder().toString();

        Assertions.assertThat(actualStringOutput).doesNotContain(PACKAGES_TO_REMOVE_FROM_STACKTRACE);
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
