package it.ettore.e2e;

import it.ettore.e2e.po.professor.ProfessorCoursesPage;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.RegisterPage;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.Assert.*;

public class Login extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;

    void clearDb() {
        repoUser.deleteAll();
    }

    @Test
    public void login() {
        clearDb();
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
        clearDb();

        driver.get(baseDomain() + "login");
        // Check that we are on the right page
        assertEquals("I'm supposed to be in /login", "/login", currentPath());

        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail("this.user@does.not.exist");
        loginPage.setPassword("AnyPasswordReally");
        loginPage.loginAsProfessor();

        // Check that we're still in /login
        assertEquals("I'm supposed to be in /login after a bad login", "/login", currentPath());
        assertEquals(Optional.of("Invalid credentials"), loginPage.getError());
    }

    @Test
    public void wrongPassword() {
        clearDb();
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
        assertEquals(Optional.of("Invalid credentials"), loginPage.getError());
    }
}
