package it.ettore.e2e.po;

import it.ettore.e2e.po.professor.ProfessorCoursesPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebElement;

import java.util.Optional;

public class RegisterPage extends PageObject {
    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(name = "first_name")
    private WebElement firstName;

    @FindBy(name = "last_name")
    private WebElement lastName;

    @FindBy(name = "email")
    private WebElement email;

    @FindBy(name = "password")
    private WebElement password;

    @FindBy(name = "confirm_password")
    private WebElement confirmPassword;

    @FindBy(xpath = "//input[@value='student']")
    private WebElement radioStudent;

    @FindBy(xpath = "//input[@value='professor']")
    private WebElement radioProfessor;

    @FindBy(id = "invalid-email-format")
    private WebElement invalidEmailFormatMsg;

    @FindBy(id = "password-too-short")
    private WebElement passwordTooShortMsg;

    @FindBy(id = "passwords-dont-match")
    private WebElement passwordsDontMatchMsg;

    @FindBy(id = "btn-register")
    private WebElement registerButton;

    @FindBy(id = "btn-goto-login")
    private WebElement gotoLoginButton;

    public boolean isInvalidEmailFormatVisible() {
        return !invalidEmailFormatMsg.getCssValue("display").equals("none");
    }

    public boolean isPasswordTooShortVisible() {
        return !passwordTooShortMsg.getCssValue("display").equals("none");
    }

    public boolean isPasswordsDontMatchVisible() {
        return !passwordsDontMatchMsg.getCssValue("display").equals("none");
    }

    public boolean isRegisterClickable() {
        return !registerButton.getCssValue("pointer-events").equals("none");
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

    public void tickStudent() {
        this.radioStudent.click();
    }

    public void tickProfessor() {
        this.radioProfessor.click();
    }

    public ProfessorCoursesPage registerAsProfessor() {
        registerButton.click();
        return new ProfessorCoursesPage(driver);
    }

    // TODO Update when student pages are created
    public Object registerAsStudent() {
        registerButton.click();
        return null;
    }

    public LoginPage returnToLogin() {
        gotoLoginButton.click();
        return new LoginPage(driver);
    }
}