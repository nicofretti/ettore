package it.ettore.e2e;

import it.ettore.model.UserRepository;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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

        //try logging in with a non-existent user
        driver.get(baseDomain() + "login");
        assertEquals("Log In", driver.getTitle());
        driver.findElement(By.name("email")).sendKeys("human.being@earth.space");
        driver.findElement(By.name("password")).sendKeys("Alien-in-disguise");
        driver.findElement(By.name("login")).click();
        //remains on the same page since the user does not exist
        assertEquals("Log In", driver.getTitle());

        //register a new user
        driver.findElement(By.name("register")).click();
        assertEquals("Register", driver.getTitle());

        driver.findElement(By.name("first_name")).sendKeys("Human");
        driver.findElement(By.name("last_name")).sendKeys("Being");
        driver.findElement(By.name("email")).sendKeys("human.being@earth.space");
        driver.findElement(By.name("password")).sendKeys("haha_gotcha");
        driver.findElement(By.name("confirm_password")).sendKeys("haha_gotcha");
        driver.findElement(By.id("btn-register")).click();
        //check if we got redirected on index.html
        assertEquals("Ettore", driver.getTitle());

        //move to the login page
        driver.get(baseDomain() + "login");
        assertEquals("Log In", driver.getTitle());
        driver.findElement(By.name("email")).sendKeys("human.being@earth.space");
        driver.findElement(By.name("password")).sendKeys("haha_gotcha");
        driver.findElement(By.name("login")).click();
        //check if we got redirected on index.html
        assertEquals("Ettore", driver.getTitle());
    }
}
