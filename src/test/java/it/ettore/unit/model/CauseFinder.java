package it.ettore.unit.model;

import it.ettore.utils.Utils;
import org.junit.Test;

import static org.junit.Assert.*;

public class CauseFinder {
    /**
     * Test that the isCause function in Utils class can correctly figure out whether an Exception class did cause the
     * given exception at any level
     */
    @Test
    public void isCause() {
        Exception e = new Exception("x", new Exception("y", new IllegalStateException("illegal state")));
        assertTrue(Utils.IsCause(e, IllegalStateException.class));
    }
}
