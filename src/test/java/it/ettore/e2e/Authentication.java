package it.ettore.e2e;

import it.ettore.e2e.po.LoginPage;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import org.junit.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class Authentication extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;

    @Test
    public void canGoToLogin() {
        // Can go freely to login page, even when unauthenticated
        driver.get(baseDomain() + "login");
        assertEquals("/login", currentPath());
    }

    @Test
    public void canGoToRegister() {
        // Can go freely to register page, even when unauthenticated
        driver.get(baseDomain() + "register");
        assertEquals("/register", currentPath());
    }

    @Test
    public void cannotGoToSecurePages() {
        // Check that we get redirected to login page
        driver.get(baseDomain() + "any-url-doesnt-matter");
        assertEquals("/login", currentPath());
    }

    @Test
    public void canGoToCoursesList() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        repoUser.save(new User("FirstName", "LastName", email, password, User.Role.PROFESSOR));

        // Login
        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.setEmail(email);
        loginPage.setPassword(password);
        loginPage.loginAsProfessor();

        // Check that we get redirected to the courses list page
        assertEquals("/professor/courses", currentPath());
    }
}
