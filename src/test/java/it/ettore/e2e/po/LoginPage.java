package it.ettore.e2e.po;

import it.ettore.e2e.po.professor.ProfessorCoursesPage;
import it.ettore.e2e.po.student.StudentCoursesPage;
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

    public StudentCoursesPage loginAsStudent() {
        loginButton.click();
        return new StudentCoursesPage(driver);
    }

    public RegisterPage register() {
        gotoRegisterButton.click();
        return new RegisterPage(driver);
    }
}
