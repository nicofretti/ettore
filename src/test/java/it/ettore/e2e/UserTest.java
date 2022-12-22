package it.ettore.e2e;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.*;

public class UserTest extends E2EBaseTest {
    @Test
    // This test is just to check that the testing framework is set up correctly
    public void user() {
        // Go to /user and check that the expected text is there
        driver.get(baseDomain() + "/user");
        WebElement element = driver.findElement(By.xpath("//p"));
        assertEquals("There is a user with id 1", element.getText());
    }
}
