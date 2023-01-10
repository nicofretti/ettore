package it.ettore.e2e.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebElement;

public class LoginPage extends PageObject{

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(tagName = "title")
    private WebElement title;

    @FindBy(name = "email")
    private WebElement email;

    @FindBy(name = "password")
    private WebElement password;

    @FindBy(name = "login")
    private WebElement loginButton;

    @FindBy(name = "register")
    private WebElement registerButton;

    public String getTitle() {
        return title.getText();
    }

    public void setEmail(String email) {
        this.email.clear();
        this.email.sendKeys(email);
    }

    public void setPassword(String password) {
        this.password.clear();
        this.password.sendKeys(password);
    }

    /*
    public CoursesPage login() {
        loginButton.click();
        return new CoursesPage(driver);
    }
    */
    public RegistrationPage register() {
        registerButton.click();
        return new RegistrationPage(driver);
    }

}
