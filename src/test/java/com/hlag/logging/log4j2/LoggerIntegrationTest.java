package com.hlag.logging.log4j2;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
class LoggerIntegrationTest {
    @Test
    void logException() {
        try {
            wrapException();
        } catch (Exception e) {
            log.throwing(e);
        }
    }

    private void wrapException() {
        try {
            throwArithmeticException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void throwArithmeticException() {
        throw new ArithmeticException("/ by zero");
    }
}
