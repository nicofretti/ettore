package it.ettore.e2e;

import it.ettore.model.User;
import it.ettore.model.UserRepository;
import org.junit.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class Authentication extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;

    void clearDb() {
        repoUser.deleteAll();
    }

    @Test
    public void testCanGoToLogin() {
        clearDb();

        // Make sure we're logged out
        driver.get(baseDomain() + "logout");

        driver.get(baseDomain() + "login");
        // Can go freely to login page, even when unauthorized
        assertEquals("Log In", driver.getTitle());
    }

    @Test
    public void testCanGoToRegister() {
        clearDb();

        // Make sure we're logged out
        driver.get(baseDomain() + "logout");

        driver.get(baseDomain() + "register");
        // Can go freely to register page, even when unauthorized
        assertEquals("Register", driver.getTitle());
    }

    @Test
    public void testCannotGoToIndex() {
        clearDb();

        // Make sure we're logged out
        driver.get(baseDomain() + "logout");

        driver.get(baseDomain());
        // Check that we get redirected to login page
        assertEquals("Log In", driver.getTitle());
    }

    @Test
    public void testCanGoToIndexWhenLoggedIn() {
        clearDb();
        repoUser.save(new User("FirstName", "LastName", "a.professor@ettore.it", "SomeSecurePassword", User.Role.PROFESSOR));

        // Login
        driver.get(baseDomain() + "login");
        driver.findElement(By.name("email")).sendKeys("a.professor@ettore.it");
        driver.findElement(By.name("password")).sendKeys("SomeSecurePassword");
        driver.findElement(By.name("login")).click();

        // Check that we get redirected to index page
        assertEquals("Ettore", driver.getTitle());
    }
}
