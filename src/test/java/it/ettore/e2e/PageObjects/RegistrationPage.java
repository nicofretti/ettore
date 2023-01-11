package it.ettore.e2e.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebElement;

public class RegistrationPage extends PageObject {

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(tagName = "title")
    private WebElement title;

    @FindBy(name = "first_name")
    private WebElement firstName;

    @FindBy(name = "last_name")
    private WebElement lastName;

    @FindBy(name = "email")
    private WebElement email;

    @FindBy(id = "email-not-ok")
    private WebElement emailNotOk;

    @FindBy(name = "password")
    private WebElement password;


    @FindBy(name = "confirm_password")
    private WebElement confirmPassword;

    @FindBy(id = "password-too-short")
    private WebElement passwordNotOk;

    @FindBy(id = "btn-register")
    private WebElement registerButton;

    @FindBy(tagName = "a")
    private WebElement returnToLogin;

    @FindBy(id = "error")
    private WebElement error;

    public String getTitle() {
        return title.getText();
    }

    public String getError() {
        return error.getText();
    }

    public String getEmailNotOk() {

        return emailNotOk.getCssValue("display");
    }
    public String getPasswordNotOk() {

        return passwordNotOk.getCssValue("display");
    }

    public String getButtonClickable() {
        return registerButton.getCssValue("pointer-events");
    }


    public void setFirstName(String firstName) {
        this.firstName.clear();
        this.firstName.sendKeys(firstName);
    }

    public void setLastName(String lastName) {
        this.lastName.clear();
        this.lastName.sendKeys(lastName);
    }

    public void setEmail(String email) {
        this.email.clear();
        this.email.sendKeys(email);
    }

    public void setPassword(String password) {
        this.password.clear();
        this.password.sendKeys(password);
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword.clear();
        this.confirmPassword.sendKeys(confirmPassword);
    }


    public CoursesPage register() {
        registerButton.click();
        return new CoursesPage(driver);
    }
    public LoginPage returnToLogin() {
        returnToLogin.click();
        return new LoginPage(driver);
    }


}