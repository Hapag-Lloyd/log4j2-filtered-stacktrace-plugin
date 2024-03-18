package com.hlag.logging.log4j2;

import com.google.common.annotations.VisibleForTesting;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolver;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverContext;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolver;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolverConfig;
import org.apache.logging.log4j.layout.template.json.util.JsonWriter;

import java.util.Arrays;
import java.util.List;

/**
 * Defines a custom resolver to remove stacktrace lines that are irrelevant.
 * See <a href="https://stackoverflow.com/questions/70614495/is-there-a-way-to-override-the-exceptionresolver-of-jsontemplatelayout-of-log4j2/77143208#77143208">Stackoverflow</a>
 *
 * @author Copied from Apache log4j2 and modified by takanuva15
 */
class FilteredStacktraceExceptionResolver implements EventResolver {
    private static List<String> packagesToRemoveFromStacktrace = Arrays.asList(
            "com.ibm.ejs",
            "com.ibm.tx",
            "com.ibm.websphere.jaxrs.",
            "com.ibm.ws",
            "java.lang",
            "java.security",
            "java.util.concurrent",
            "javax.servlet",
            "jdk.internal",
            "org.apache.cxf",
            "org.hibernate.validator.cdi",
            "org.jboss.weld");
    private final TemplateResolver<Throwable> internalResolver;

    FilteredStacktraceExceptionResolver(EventResolverContext context, TemplateResolverConfig config) {
        this.internalResolver = new FilteredStacktraceStackTraceJsonResolver(context, packagesToRemoveFromStacktrace);
    }

    @VisibleForTesting
    static void setPackagesToRemoveFromStacktrace(List<String> packagesToRemoveFromStacktrace) {
        FilteredStacktraceExceptionResolver.packagesToRemoveFromStacktrace = packagesToRemoveFromStacktrace;
    }

    @Override
    public void resolve(LogEvent logEvent, JsonWriter jsonWriter) {
        final Throwable exception = logEvent.getThrown();

        if (exception == null) {
            jsonWriter.writeNull();
        } else {
            internalResolver.resolve(exception, jsonWriter);
        }
    }

    @Override
    public boolean isResolvable(final LogEvent logEvent) {
        return logEvent.getThrown() != null;
    }
}
