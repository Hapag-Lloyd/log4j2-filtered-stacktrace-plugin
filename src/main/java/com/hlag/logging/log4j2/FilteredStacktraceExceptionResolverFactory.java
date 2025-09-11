package com.hlag.logging.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.layout.template.json.resolver.*;

/**
 * Defines a custom resolver to remove stacktrace lines that are irrelevant.
 * See <a href="https://stackoverflow.com/questions/70614495/is-there-a-way-to-override-the-exceptionresolver-of-jsontemplatelayout-of-log4j2/77143208#77143208">Stackoverflow</a>
 *
 * @author Copied from Apache log4j2 and modified by takanuva15
 */
@Plugin(name = "FilteredStacktraceExceptionResolverFactory", category = TemplateResolverFactory.CATEGORY)
@SuppressWarnings("java:S6548") // singleton, as suggested by Apache log4j2 documentation.
public final class FilteredStacktraceExceptionResolverFactory implements EventResolverFactory {
    private static final FilteredStacktraceExceptionResolverFactory INSTANCE = new FilteredStacktraceExceptionResolverFactory();

    private FilteredStacktraceExceptionResolverFactory() {
    }

    /**
     * Create the singleton instance of the plugin factory.
     *
     * @return the singleton instance of the plugin factory.
     */
    @PluginFactory
    public static FilteredStacktraceExceptionResolverFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public String getName() {
        return "filteredStacktraceException";
    }

    @Override
    public TemplateResolver<LogEvent> create(EventResolverContext context, TemplateResolverConfig config) {
        return new FilteredStacktraceExceptionResolver(context, config);
    }
}
