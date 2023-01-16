package it.ettore.unit.utils;

import it.ettore.utils.Error;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ErrorTest {
    @Test
    public void testToString() {
        Error r = new Error("message");
        assertEquals("Error{message}", r.toString());
    }

}
