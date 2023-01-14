package it.ettore.e2e.po.student;

import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.PageObject;
import it.ettore.e2e.po.professor.ProfessorManagePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class StudentCoursePage extends PageObject {
    public StudentCoursePage(WebDriver driver) {
        super(driver);
    }

    public Header headerComponent() {
        return new Header(driver);
    }

    @FindBy(className = "et-name")
    private WebElement name;

    @FindBy(className = "et-period")
    private WebElement period;

    @FindBy(className = "et-description")
    private WebElement description;

    @FindBy(id = "btn-unjoin")
    private WebElement unjoinButton;

    public String getName() {
        return name.getText();
    }

    public String getPeriod() {
        return period.getText();
    }

    public String getDescription() {
        return description.getText();
    }

    public StudentCoursesPage unjoin() {
        unjoinButton.click();
        return new StudentCoursesPage(driver);
    }
}
