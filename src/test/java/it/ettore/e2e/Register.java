package it.ettore.e2e;

import it.ettore.e2e.po.professor.ProfessorCoursesPage;
import it.ettore.e2e.po.RegisterPage;
import it.ettore.model.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.Assert.*;

public class Register extends E2EBaseTest {
    @Test
    public void allFieldsMustBeFilled() {
        driver.get(baseDomain() + "register");
        assertEquals("I'm supposed to be in /register", "/register", currentPath());
        RegisterPage registerPage = new RegisterPage(driver);

        Runnable assertNotClickable = () -> {
            assertFalse("Register button shouldn't be clickable yet", registerPage.isRegisterClickable());
        };

        // Assert button is not clickable initially
        assertNotClickable.run();

        // One by one, insert the data and check that the button becomes clickable only at the end
        registerPage.setFirstName("Human");
        assertNotClickable.run();

        registerPage.setLastName("Being");
        assertNotClickable.run();

        registerPage.setEmail("human.being@earth.space");
        assertNotClickable.run();

        registerPage.setPassword("haha_gotcha");
        assertNotClickable.run();

        registerPage.setConfirmPassword("haha_gotcha");

        // Now clickable
        assertTrue("Register button should be clickable when all fields are filled out", registerPage.isRegisterClickable());

        registerPage.tickProfessor();
        registerPage.registerAsProfessor();
        assertEquals("I'm supposed to be in /professor/courses", "/professor/courses", currentPath());
    }

    @Test
    public void emailMustBeValid() {
        driver.get(baseDomain() + "register");
        assertEquals("I'm supposed to be in /register", "/register", currentPath());
        RegisterPage registerPage = new RegisterPage(driver);

        // Start registering a new user with an invalid email
        registerPage.setFirstName("Definitely");
        registerPage.setLastName("Real Human");
        registerPage.setEmail("earth_invasion_attempt");
        registerPage.setPassword("alien_spy");
        registerPage.setConfirmPassword("alien_spy");

        // Assert error is shown
        assertTrue(registerPage.isInvalidEmailFormatVisible());
        // Assert button is not clickable
        assertFalse(registerPage.isRegisterClickable());

        // Now insert a valid email
        registerPage.setEmail("these.humans@are.so.smart.com");

        // Assert error is not shown anymore
        assertFalse(registerPage.isInvalidEmailFormatVisible());

        // Assert button is now clickable
        assertTrue(registerPage.isRegisterClickable());

        // Click the register button and conclude the registration
        registerPage.tickProfessor();
        registerPage.registerAsProfessor();

        // Check that the registration redirected us correctly
        assertEquals("I'm supposed to be in /professor/courses", "/professor/courses", currentPath());
    }

    @Test
    public void passwordMustBeLongEnough() {
        driver.get(baseDomain() + "register");
        assertEquals("I'm supposed to be in /register", "/register", currentPath());
        RegisterPage registerPage = new RegisterPage(driver);

        // Start registering a new user with a short password
        registerPage.setFirstName("Definitely");
        registerPage.setLastName("Real Human");
        registerPage.setEmail("earth@space.com");
        registerPage.setPassword("spy");
        registerPage.setConfirmPassword("spy");

        // Assert error is shown
        assertTrue(registerPage.isPasswordTooShortVisible());
        // Assert button is not clickable
        assertFalse(registerPage.isRegisterClickable());

        // Now let's try with a long enough password
        registerPage.setPassword("alien_spy");
        registerPage.setConfirmPassword("alien_spy");

        // Assert error is not shown anymore
        assertFalse(registerPage.isPasswordTooShortVisible());
        // Assert button is now clickable
        assertTrue(registerPage.isRegisterClickable());

        // Click the register button and conclude the registration
        registerPage.tickProfessor();
        registerPage.registerAsProfessor();
        assertEquals("I'm supposed to be in /professor/courses", "/professor/courses", currentPath());
    }

    @Test
    public void passwordsMustMatch() {
        driver.get(baseDomain() + "register");
        assertEquals("I'm supposed to be in /register", "/register", currentPath());
        RegisterPage registerPage = new RegisterPage(driver);

        // Start registering a new user with a short password
        registerPage.setFirstName("Definitely");
        registerPage.setLastName("Real Human");
        registerPage.setEmail("earth@space.com");
        registerPage.setPassword("alien_spy");
        registerPage.setConfirmPassword("alien_spy_ohno");

        // Assert error is shown
        assertTrue(registerPage.isPasswordsDontMatchVisible());
        // Assert button is not clickable
        assertFalse(registerPage.isRegisterClickable());

        // Now use the same password
        registerPage.setConfirmPassword("alien_spy");

        // Assert error is not shown anymore
        assertFalse(registerPage.isPasswordsDontMatchVisible());
        // Assert button is now clickable
        assertTrue(registerPage.isRegisterClickable());

        // Click the register button and conclude the registration
        registerPage.tickProfessor();
        registerPage.registerAsProfessor();
        assertEquals("I'm supposed to be in /professor/courses", "/professor/courses", currentPath());
    }

    @Test
    public void cannotRegisterSameEmailTwice() {
        driver.get(baseDomain() + "register");
        assertEquals("I'm supposed to be in /register", "/register", currentPath());
        RegisterPage registerPage = new RegisterPage(driver);

        // Register once
        registerPage.setFirstName("Real");
        registerPage.setLastName("Human");
        registerPage.setEmail("real.human@earth.com");
        registerPage.setPassword("alien_spy");
        registerPage.setConfirmPassword("alien_spy");

        registerPage.tickProfessor();
        registerPage.registerAsProfessor();
        assertEquals("I'm supposed to be in /professor/courses", "/professor/courses", currentPath());

        driver.get(baseDomain() + "register");
        registerPage = new RegisterPage(driver);
        assertEquals("I'm supposed to be in /register", "/register", currentPath());

        // Try to register again, use the same email
        registerPage.setFirstName("Definitely Human");
        registerPage.setLastName("Being");
        registerPage.setEmail("real.human@earth.com");
        registerPage.setPassword("alien_spy");
        registerPage.setConfirmPassword("alien_spy");

        registerPage.tickProfessor();
        registerPage.registerAsProfessor();

        // Should be still in /register
        assertEquals("I'm supposed to be in /register", "/register", currentPath());
        // Assert error is shown
        assertEquals(Optional.of("Email already taken"), registerPage.getError());
    }
}
