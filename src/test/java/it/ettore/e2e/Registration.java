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



    void clearDb() {
        repoUser.deleteAll();
    }

    @Test
    public void allFieldsMustBeFilled() {
        clearDb();

        driver.get(baseDomain() + "register");
        assertEquals("Register", driver.getTitle());

        RegistrationPage registrationPage = new RegistrationPage(this.driver);

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

        // TODO:Click the button
        //CoursesPage coursesPage = registrationPage.register();
        //assertEquals("Ettore", coursesPage.getTitle());

    }

    @Test
    public void emailMustBeValid() {
        clearDb();

        driver.get(baseDomain() + "register");
        RegistrationPage registrationPage = new RegistrationPage(this.driver);

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
        //assertEquals("Ettore",coursesPage.getTitle());
    }

    @Test
    public void passwordMustBeLongEnough() {
        clearDb();

        driver.get(baseDomain() + "register");
        RegistrationPage registrationPage = new RegistrationPage(this.driver);
        //assertEquals("Register",registrationPage.getTitle()); //maybe delete bcs its repetitive even in other tests?

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
        //assertEquals("Ettore",coursesPage.getTitle());
    }

    @Test
    public void cannotRegisterSameEmailTwice() {
        clearDb();
        driver.get(baseDomain() + "register");
        RegistrationPage registrationPage = new RegistrationPage(this.driver);
        //assertEquals("Register", registrationPage.getTitle());

        // Register once
        registrationPage.setFirstName("Real");
        registrationPage.setLastName("Human");
        registrationPage.setEmail("real.human@earth.com");
        registrationPage.setPassword("alien_spy");
        registrationPage.setConfirmPassword("alien_spy");

        CoursesPage coursesPage = registrationPage.register();
        //assertEquals("Ettore",coursesPage.getTitle());

        //TODO should i change the driver url like this or use another way?
        driver.get(baseDomain() + "register");
        registrationPage = new RegistrationPage(this.driver);
        //assertEquals("Register", registrationPage.getTitle());

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
        //assertEquals("Register", registrationPage.getTitle());
        assertEquals("Email already taken", registrationPage.getError());
    }
}
