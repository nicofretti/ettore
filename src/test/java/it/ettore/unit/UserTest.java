package it.ettore.unit;

import it.ettore.model.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
    @Test
    public void testDefaultUser() {
        User user = new User();
        assertEquals("User{id=0}", user.toString());
    }
}
