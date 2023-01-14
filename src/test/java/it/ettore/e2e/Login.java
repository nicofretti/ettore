package it.ettore.e2e;

import it.ettore.e2e.po.ErrorsComponent;
import it.ettore.e2e.po.professor.ProfessorCoursesPage;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.RegisterPage;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class Login extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;

    @Test
    public void login() {
        String email = "some.user@ettore.it";
        String password = "SomeSecurePassword";
        repoUser.save(new User("First", "Last", email, password, User.Role.PROFESSOR));

        driver.get(baseDomain() + "login");
        // Check that we are on the right page
        assertEquals("I'm supposed to be in /login", "/login", currentPath());

        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);
        loginPage.loginAsProfessor();

        // Check that we're logged in and have been redirected to our homepage
        assertEquals("I'm supposed to be in /professor/courses", "/professor/courses", currentPath());
    }

    @Test
    public void noSuchUser() {
        driver.get(baseDomain() + "login");
        // Check that we are on the right page
        assertEquals("I'm supposed to be in /login", "/login", currentPath());

        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail("this.user@does.not.exist");
        loginPage.setPassword("AnyPasswordReally");
        loginPage.loginAsProfessor();

        // Check that we're still in /login
        assertEquals("I'm supposed to be in /login after a bad login", "/login", currentPath());
        ErrorsComponent errors = new ErrorsComponent(driver);
        assertEquals(Set.of("Invalid credentials"), errors.getErrorMessageSet());
        // Dismiss the error
        errors.getErrors().get(0).dismiss();
        // Should now have no errors displayed
        assertEquals(Set.of(), errors.getErrorMessageSet());
    }

    @Test
    public void wrongPassword() {
        String email = "some.user@ettore.it";
        String password = "SomeSecurePassword";
        repoUser.save(new User("First", "Last", email, password, User.Role.PROFESSOR));

        driver.get(baseDomain() + "login");
        // Check that we are on the right page
        assertEquals("I'm supposed to be in /login", "/login", currentPath());

        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password + "oh no now the password is wrong :(");
        loginPage.loginAsProfessor();

        // Check that we're still in /login
        assertEquals("I'm supposed to be in /login after a bad login", "/login", currentPath());
        ErrorsComponent errors = new ErrorsComponent(driver);
        assertEquals(Set.of("Invalid credentials"), errors.getErrorMessageSet());
        // Dismiss the error
        errors.getErrors().get(0).dismiss();
        // Should now have no errors displayed
        assertEquals(Set.of(), errors.getErrorMessageSet());
    }
}
