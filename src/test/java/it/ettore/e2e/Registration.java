package it.ettore.e2e;

import it.ettore.e2e.PageObjects.CoursesPage;
import it.ettore.e2e.PageObjects.RegistrationPage;
import it.ettore.model.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class Registration extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;

    public String url () {
        String url = driver.getCurrentUrl();
        return url.substring(url.lastIndexOf("/"));
    }

    void clearDb() {
        repoUser.deleteAll();
    }

    @Test
    public void allFieldsMustBeFilled() {
        clearDb();

        driver.get(baseDomain() + "register");
        RegistrationPage registrationPage = new RegistrationPage(this.driver);
        assertEquals("I'm supposed to be in /register" ,"/register", url());


        Runnable assertNotClickable = () -> {
            assertEquals("Oh, shit i am not supposed to be clickable yet","none", registrationPage.getButtonClickable());
        };

        // Assert button is not clickable initially
        assertNotClickable.run();

        // One by one, insert the data and check that the button becomes clickable only at the end
        registrationPage.setFirstName("Human");
        assertNotClickable.run();

        registrationPage.setLastName("Being");
        assertNotClickable.run();

        registrationPage.setEmail("human.being@earth.space");
        assertNotClickable.run();

        registrationPage.setPassword("haha_gotcha");
        assertNotClickable.run();

        registrationPage.setConfirmPassword("haha_gotcha");
        // Now clickable
        assertNotEquals("I should be clickable by now","none", registrationPage.getButtonClickable());

        CoursesPage coursesPage = registrationPage.register();
        assertEquals("I'm supposed to be in /courses" ,"/courses", url());

    }

    @Test
    public void emailMustBeValid() {
        clearDb();

        driver.get(baseDomain() + "register");
        RegistrationPage registrationPage = new RegistrationPage(this.driver);
        assertEquals("I'm supposed to be in /register" ,"/register", url());


        // Start registering a new user with an invalid email
        registrationPage.setFirstName("Definitely");
        registrationPage.setLastName("Real Human");
        registrationPage.setEmail("earth_invasion_attempt");
        registrationPage.setPassword("alien_spy");
        registrationPage.setConfirmPassword("alien_spy");

        // Assert error is shown
        assertNotEquals("none", registrationPage.getEmailNotOk());
        // Assert button is not clickable
        assertEquals("Oh, shit i am not supposed to be clickable","none", registrationPage.getButtonClickable());

        // Now insert a valid email
        registrationPage.setEmail("these.humans@are.so.smart.com");

        // Assert error is not shown anymore
        assertEquals("none",registrationPage.getEmailNotOk());

        // Assert button is now clickable
        assertNotEquals("none",registrationPage.getButtonClickable());

        //click the register button and conclude the registration
        CoursesPage coursesPage = registrationPage.register();

        //check that the registration redirected us correctly
        assertEquals("I'm supposed to be in /courses" ,"/courses", url());
    }

    @Test
    public void passwordMustBeLongEnough() {
        clearDb();

        driver.get(baseDomain() + "register");
        RegistrationPage registrationPage = new RegistrationPage(this.driver);
        assertEquals("I'm supposed to be in /register" ,"/register", url());

        // Start registering a new user with an invalid email
        registrationPage.setFirstName("Definitely");
        registrationPage.setLastName("Real Human");
        registrationPage.setEmail("earth@space.com");
        registrationPage.setPassword("spy");
        registrationPage.setConfirmPassword("spy");

        // Assert error is shown
        assertNotEquals("none", registrationPage.getPasswordNotOk());
        // Assert button is not clickable
        assertEquals("none",registrationPage.getButtonClickable());

        // Now let's try with a long enough password
        registrationPage.setPassword("alien_spy");
        registrationPage.setConfirmPassword("alien_spy");

        // Assert error is not shown anymore
        assertEquals("none",registrationPage.getPasswordNotOk());
        // Assert button is now clickable
        assertNotEquals("none",registrationPage.getButtonClickable());

        //click the register button and conclude the registration
        CoursesPage coursesPage = registrationPage.register();
        assertEquals("I'm supposed to be in /register" ,"/register", url());
    }

    @Test
    public void cannotRegisterSameEmailTwice() {
        clearDb();
        driver.get(baseDomain() + "register");
        RegistrationPage registrationPage = new RegistrationPage(this.driver);
        assertEquals("I'm supposed to be in /register" ,"/register", url());

        // Register once
        registrationPage.setFirstName("Real");
        registrationPage.setLastName("Human");
        registrationPage.setEmail("real.human@earth.com");
        registrationPage.setPassword("alien_spy");
        registrationPage.setConfirmPassword("alien_spy");

        registrationPage.register();
        assertEquals("I'm supposed to be in /courses" ,"/courses", url());

        driver.get(baseDomain() + "register");
        registrationPage = new RegistrationPage(this.driver);
        assertEquals("I'm supposed to be in /register" ,"/register", url());

        // Try to register again, use the same email
        registrationPage.setFirstName("Definitely Human");
        registrationPage.setLastName("Being");
        registrationPage.setEmail("real.human@earth.com");
        registrationPage.setPassword("alien_spy");
        registrationPage.setConfirmPassword("alien_spy");

        // Assert error is shown
        registrationPage.register();
        //TODO: hmm in this case should i still save the return value? or just assert the error?
        // Assert we're still on register
        assertEquals("I'm supposed to remain in /register" ,"/register", url());
        assertEquals("Email already taken", registrationPage.getError());
    }
}
