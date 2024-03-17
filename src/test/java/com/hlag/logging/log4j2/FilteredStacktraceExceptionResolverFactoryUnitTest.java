package com.hlag.logging.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverContext;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolver;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolverConfig;
import org.apache.logging.log4j.layout.template.json.util.QueueingRecyclerFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;

@ExtendWith(MockitoExtension.class)
class FilteredStacktraceExceptionResolverFactoryUnitTest {
    private FilteredStacktraceExceptionResolverFactory factory;

    @Mock
    private TemplateResolverConfig mockedTemplateResolverConfig;

    @Mock
    private EventResolverContext mockedEventResolverContext;

    @BeforeEach
    void setUp() {
        factory = FilteredStacktraceExceptionResolverFactory.getInstance();
    }

    @Test
    void shouldReturnAFilteredStacktraceExceptionResolver_whenCreate() {
        Mockito.when(mockedEventResolverContext.getRecyclerFactory()).thenReturn(new QueueingRecyclerFactory(LinkedList::new));

        TemplateResolver<LogEvent> actualResolver = factory.create(mockedEventResolverContext, mockedTemplateResolverConfig);

        Assertions.assertThat(actualResolver).isInstanceOf(FilteredStacktraceExceptionResolver.class);
    }

    @Test
    void shouldCreateANewResolver_whenCreate_givenMultipleCalls() {
        Mockito.when(mockedEventResolverContext.getRecyclerFactory()).thenReturn(new QueueingRecyclerFactory(LinkedList::new));

        TemplateResolver<LogEvent> actualFirstResolver = factory.create(mockedEventResolverContext, mockedTemplateResolverConfig);
        TemplateResolver<LogEvent> actualSecondResolver = factory.create(mockedEventResolverContext, mockedTemplateResolverConfig);

        Assertions.assertThat(actualFirstResolver).isNotSameAs(actualSecondResolver);
    }

    @Test
    void shouldReturnTheNameUsedInConfigFiles_whenGetName() {
        String actualName = factory.getName();

        Assertions.assertThat(actualName).isEqualTo("filteredStacktraceException");
    }

    @Test
    void shouldCreateOneInstanceOnly_whenGetInstance_givenMultipleCalls() {
        FilteredStacktraceExceptionResolverFactory actualFactory = FilteredStacktraceExceptionResolverFactory.getInstance();

        Assertions.assertThat(actualFactory).isSameAs(factory);
    }

    @Test
    void shouldAlwaysCreateAFactory_whenGetInstance() {
        FilteredStacktraceExceptionResolverFactory actualFactory = FilteredStacktraceExceptionResolverFactory.getInstance();

        Assertions.assertThat(actualFactory).isNotNull();
    }
}
