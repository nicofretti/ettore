package it.ettore;

import java.util.Collection;

import static org.junit.Assert.*;

public class TestUtil {
    public static void assertEmpty(Collection<?> coll) {
        assertTrue("Expected collection to be empty but was not", coll == null || coll.size() == 0);
    }
}
