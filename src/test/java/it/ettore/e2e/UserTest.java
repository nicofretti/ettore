package it.ettore.e2e;

import it.ettore.e2e.PageObjects.CoursesPage;
import it.ettore.e2e.PageObjects.LoginPage;
import it.ettore.e2e.PageObjects.RegistrationPage;
import it.ettore.model.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class UserTest extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;

    void clearDb() {
        repoUser.deleteAll();
    }

    @Test
    public void userRegisteringAndLoggingIn() {
        clearDb();
        driver.get(baseDomain() + "login");
        //System.out.println(driver.getCurrentUrl());
        LoginPage loginPage = new LoginPage(driver);

        //TODO: delete this later
        assertEquals("I'm supposed to be in /login" ,"Log In", driver.getTitle());

        //check that we are on the right page

        assertEquals("I'm supposed to be in /login" ,"Log In", loginPage.getTitle());

        //first we try logging in with a non-existent user
        loginPage.setEmail("human.being@earth.space");
        loginPage.setPassword("Alien-in-disguise");
        loginPage.login();

        //check that we are still on the same page
        assertEquals("I'm supposed to be in /login" ,"Log In", loginPage.getTitle());

        //now we try registering a new user
        RegistrationPage registrationPage = loginPage.register();
        assertEquals("I'm supposed to be in /register" ,"Register", registrationPage.getTitle());

        //filling the form
        registrationPage.setFirstName("Real");
        registrationPage.setLastName("Human");
        registrationPage.setEmail("real_human@earth.com");
        registrationPage.setPassword("alien_spy");
        registrationPage.setConfirmPassword("alien_spy");

        //TODO: add the rest of the test when the other pages are ready
        //loginPage = registrationPage.register();
        assertEquals("I'm supposed to be in /login" ,"Log In", loginPage.getTitle());

        //now we try logging in with the new user
        loginPage.setEmail("real_human@earth.com");
        loginPage.setPassword("alien_spy");
        CoursesPage coursesPage = loginPage.login();
        assertEquals("I'm supposed to be in /courses" ,"Courses", coursesPage.getTitle());

    }
}
