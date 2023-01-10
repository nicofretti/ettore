package it.ettore.e2e;

import it.ettore.model.UserRepository;
import org.junit.Test;
import org.openqa.selenium.By;
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

        Runnable assertNotClickable = () -> {
            assertEquals("none", driver.findElement(By.id("btn-register")).getCssValue("pointer-events"));
        };

        // Assert button is not clickable initially
        assertNotClickable.run();

        // One by one, insert the data and check that the button becomes clickable only at the end

        driver.findElement(By.name("first_name")).sendKeys("Human");
        assertNotClickable.run();

        driver.findElement(By.name("last_name")).sendKeys("Being");
        assertNotClickable.run();

        driver.findElement(By.name("email")).sendKeys("human.being@earth.space");
        assertNotClickable.run();

        driver.findElement(By.name("password")).sendKeys("haha_gotcha");
        assertNotClickable.run();

        driver.findElement(By.name("confirm_password")).sendKeys("haha_gotcha");

        // Now clickable
        assertNotEquals("none", driver.findElement(By.id("btn-register")).getCssValue("pointer-events"));

        driver.findElement(By.tagName("button")).click();

        // Assert we got redirected to index.html
        assertEquals("Ettore", driver.getTitle());
    }

    @Test
    public void emailMustBeValid() {
        clearDb();

        driver.get(baseDomain() + "register");
        assertEquals("Register", driver.getTitle());
        driver.findElement(By.name("first_name")).sendKeys("Human");
        driver.findElement(By.name("last_name")).sendKeys("Being");
        driver.findElement(By.name("email")).sendKeys("not_an_email");
        driver.findElement(By.name("password")).sendKeys("haha_gotcha");
        driver.findElement(By.name("confirm_password")).sendKeys("haha_gotcha");

        // Assert error is shown
        assertNotEquals("none", driver.findElement(By.id("email-not-ok")).getCssValue("display"));
        // Assert button is not clickable
        assertEquals("none", driver.findElement(By.id("btn-register")).getCssValue("pointer-events"));

        driver.findElement(By.name("email")).clear();
        driver.findElement(By.name("email")).sendKeys("human.being@earth.space");

        // Assert error is not shown anymore
        assertEquals("none", driver.findElement(By.id("email-not-ok")).getCssValue("display"));
        // Assert button is now clickable
        assertNotEquals("none", driver.findElement(By.id("btn-register")).getCssValue("pointer-events"));

        driver.findElement(By.tagName("button")).click();

        // Assert we got redirected to index.html
        assertEquals("Ettore", driver.getTitle());
    }

    @Test
    public void passwordMustBeLongEnough() {
        clearDb();

        driver.get(baseDomain() + "register");
        assertEquals("Register", driver.getTitle());
        driver.findElement(By.name("first_name")).sendKeys("Human");
        driver.findElement(By.name("last_name")).sendKeys("Being");
        driver.findElement(By.name("email")).sendKeys("human.being@earth.space");
        driver.findElement(By.name("password")).sendKeys("short");
        driver.findElement(By.name("confirm_password")).sendKeys("short");

        // Assert error is shown
        assertNotEquals("none", driver.findElement(By.id("password-too-short")).getCssValue("display"));
        // Assert button is not clickable
        assertEquals("none", driver.findElement(By.id("btn-register")).getCssValue("pointer-events"));

        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys("haha_gotcha");
        driver.findElement(By.name("confirm_password")).clear();
        driver.findElement(By.name("confirm_password")).sendKeys("haha_gotcha");

        // Assert error is not shown anymore
        assertEquals("none", driver.findElement(By.id("password-too-short")).getCssValue("display"));
        // Assert button is now clickable
        assertNotEquals("none", driver.findElement(By.id("btn-register")).getCssValue("pointer-events"));

        driver.findElement(By.tagName("button")).click();

        // Assert we got redirected to index.html
        assertEquals("Ettore", driver.getTitle());
    }

    @Test
    public void cannotRegisterSameEmailTwice() {
        clearDb();

        // Register once
        driver.get(baseDomain() + "register");
        assertEquals("Register", driver.getTitle());
        driver.findElement(By.name("first_name")).sendKeys("Human");
        driver.findElement(By.name("last_name")).sendKeys("Being");
        driver.findElement(By.name("email")).sendKeys("human.being@earth.space");
        driver.findElement(By.name("password")).sendKeys("haha_gotcha");
        driver.findElement(By.name("confirm_password")).sendKeys("haha_gotcha");

        driver.findElement(By.tagName("button")).click();

        // Try to register again, use the same email
        driver.get(baseDomain() + "register");
        assertEquals("Register", driver.getTitle());
        driver.findElement(By.name("first_name")).sendKeys("Another Human");
        driver.findElement(By.name("last_name")).sendKeys("Being Just Like Before");
        driver.findElement(By.name("email")).sendKeys("human.being@earth.space");
        driver.findElement(By.name("password")).sendKeys("haha_gotcha_again");
        driver.findElement(By.name("confirm_password")).sendKeys("haha_gotcha_again");

        driver.findElement(By.tagName("button")).click();

        // Assert we're still on register
        assertEquals("Register", driver.getTitle());
        assertTrue(driver.findElement(By.id("error")).getText().equals("Email already taken"));
    }
}
