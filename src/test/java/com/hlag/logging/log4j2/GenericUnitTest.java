package com.hlag.logging.log4j2;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenericUnitTest {
    @Test
    void shouldCreateJava8CompatibleClasses_whenCompile_AsLog4jStillSupportsJava8() throws Exception {
        // Replace with the class you want to test
        String className = getClass().getName();

        try (InputStream in = ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class")) {
            ClassReader classReader = new ClassReader(in);
            // Java 8 corresponds to major version 52
            assertEquals(52, classReader.readUnsignedShort(6));
        }
    }
}
