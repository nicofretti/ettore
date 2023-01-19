package it.ettore.e2e;

import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import org.junit.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class Authentication extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;

    /**
     * When logged out, a user should be able to navigate to /login freely
     */
    @Test
    public void canGoToLogin() {
        // Can go freely to login page, even when unauthenticated
        driver.get(baseDomain() + "login");
        assertEquals("/login", currentPath());
    }

    /**
     * When logged out, a user should be able to navigate to /register freely
     */
    @Test
    public void canGoToRegister() {
        // Can go freely to register page, even when unauthenticated
        driver.get(baseDomain() + "register");
        assertEquals("/register", currentPath());
    }

    /**
     * When logged out, a user should not be able to navigate to any other page except from /login, /register (and
     * also get /style.css). This also includes non-existing pages. He/She should be redirected to /login if that
     * happens.
     */
    @Test
    public void cannotGoToSecurePages() {
        // Check that we get redirected to login page
        driver.get(baseDomain() + "any-url-doesnt-matter");
        assertEquals("/login", currentPath());
        driver.get(baseDomain() + "professor/courses");
        assertEquals("/login", currentPath());
    }

    /**
     * Once logged in, a professor can go to its homepage
     */
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

    /**
     * Once logged in, a user shouldn't be able to go to /login. He/She should be redirected to his/her homepage
     * instead
     */
    @Test
    public void onceLoggedInCannotLoginAgain() {
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

        driver.get(baseDomain() + "login");

        // Check that we get redirected to the courses list page instead of going to /login
        assertEquals("/professor/courses", currentPath());
    }

    /**
     * Once logged in, a user shouldn't be able to go to /register. He/She should be redirected to his/her homepage
     * instead
     */
    @Test
    public void onceLoggedInCannotRegisterAgain() {
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

        driver.get(baseDomain() + "register");

        // Check that we get redirected to the courses list page instead of going to /register
        assertEquals("/professor/courses", currentPath());
    }

    /**
     * A user should be able to logout. Which means being redirected to /login and not being able to see any another
     * parts of Ettore
     */
    @Test
    public void canLogout() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        repoUser.save(new User("FirstName", "LastName", email, password, User.Role.PROFESSOR));

        // Login
        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.setEmail(email);
        loginPage.setPassword(password);
        ProfessorCoursesPage coursesPage = loginPage.loginAsProfessor();

        // Check that we get redirected to the courses list page
        assertEquals("/professor/courses", currentPath());

        coursesPage.headerComponent().logout();

        // Check that we're in /login
        assertEquals("/login", currentPath());

        // And cannot go to authenticated pages
        driver.get(baseDomain() + "professor/courses");
        assertEquals("/login", currentPath());
    }

    /**
     * A professor should not be able to navigate or make requests to the student's side of Ettore
     */
    @Test
    public void professorCannotGoToStudentSection() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        repoUser.save(new User("FirstName", "LastName", email, password, User.Role.PROFESSOR));

        // Login
        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.setEmail(email);
        loginPage.setPassword(password);
        loginPage.loginAsProfessor();

        // Try to go to the student's side
        driver.get(baseDomain() + "student/courses");

        // We should have been redirected to our homepage
        assertEquals("/professor/courses", currentPath());
    }

    /**
     * A student should not be able to navigate or make requests to the professor's side of Ettore
     */
    @Test
    public void studentCannotGoToProfessorSection() {
        String email = "some.student@ettore.it";
        String password = "SomeSecurePassword";
        repoUser.save(new User("FirstName", "LastName", email, password, User.Role.STUDENT));

        // Login
        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.setEmail(email);
        loginPage.setPassword(password);
        loginPage.loginAsProfessor();

        // Try to go to the professor's side
        driver.get(baseDomain() + "professor/courses");

        // We should have been redirected to our homepage
        assertEquals("/student/courses", currentPath());
    }
}
