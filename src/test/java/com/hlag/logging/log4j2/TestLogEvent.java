package com.hlag.logging.log4j2;

import lombok.Builder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

import java.util.Map;

@Builder
public class TestLogEvent implements LogEvent {
    private Throwable thrown;

    public TestLogEvent(Throwable thrown) {
        this.thrown = thrown;
    }

    @Override
    public LogEvent toImmutable() {
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public Map<String, String> getContextMap() {
        return null;
    }

    @Override
    public ReadOnlyStringMap getContextData() {
        return null;
    }

    @Override
    public ThreadContext.ContextStack getContextStack() {
        return null;
    }

    @Override
    public String getLoggerFqcn() {
        return null;
    }

    @Override
    public Level getLevel() {
        return null;
    }

    @Override
    public String getLoggerName() {
        return null;
    }

    @Override
    public Marker getMarker() {
        return null;
    }

    @Override
    public Message getMessage() {
        return null;
    }

    @Override
    public long getTimeMillis() {
        return 0;
    }

    @Override
    public Instant getInstant() {
        return null;
    }

    @Override
    public StackTraceElement getSource() {
        return null;
    }

    @Override
    public String getThreadName() {
        return null;
    }

    @Override
    public long getThreadId() {
        return 0;
    }

    @Override
    public int getThreadPriority() {
        return 0;
    }

    @Override
    public Throwable getThrown() {
        return thrown;
    }

    @Override
    public ThrowableProxy getThrownProxy() {
        return null;
    }

    @Override
    public boolean isEndOfBatch() {
        return false;
    }

    @Override
    public boolean isIncludeLocation() {
        return false;
    }

    @Override
    public void setEndOfBatch(boolean endOfBatch) {

    }

    @Override
    public void setIncludeLocation(boolean locationRequired) {

    }

    @Override
    public long getNanoTime() {
        return 0;
    }
}
