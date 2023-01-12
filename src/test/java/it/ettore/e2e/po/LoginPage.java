package it.ettore.e2e.po;

import it.ettore.e2e.po.professor.ProfessorCoursesPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebElement;

import java.util.Optional;

public class LoginPage extends PageObject{
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(name = "email")
    private WebElement email;

    @FindBy(name = "password")
    private WebElement password;

    @FindBy(id = "invalid-email-format")
    private WebElement invalidEmailFormatMsg;

    @FindBy(id = "password-too-short")
    private WebElement passwordTooShortMsg;

    @FindBy(id = "btn-login")
    private WebElement loginButton;

    @FindBy(name = "btn-goto-register")
    private WebElement gotoRegisterButton;

    @FindBy(id = "error")
    private WebElement errorMsg;

    public Optional<String> getError() {
        if (errorMsg != null) {
            return Optional.of(errorMsg.getText());
        } else {
            return Optional.empty();
        }
    }

    public void setEmail(String email) {
        this.email.clear();
        this.email.sendKeys(email);
    }

    public void setPassword(String password) {
        this.password.clear();
        this.password.sendKeys(password);
    }

    public ProfessorCoursesPage loginAsProfessor() {
        loginButton.click();
        return new ProfessorCoursesPage(driver);
    }

    // TODO Update when student pages are created
    public Object loginAsStudent() {
        loginButton.click();
        return null;
    }

    public RegisterPage register() {
        gotoRegisterButton.click();
        return new RegisterPage(driver);
    }
}
