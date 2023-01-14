package it.ettore.e2e.po.professor.courses;

import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.PageObject;
import it.ettore.e2e.po.professor.ProfessorManagePage;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonsPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfessorAddsCoursePage extends PageObject {

    public ProfessorAddsCoursePage(WebDriver driver) {
        super(driver);
    }

    public Header headerComponent() {
        return new Header(driver);
    }

    @FindBy(name = "category")
    private WebElement category;

    @FindBy(name = "name")
    private WebElement courseName;

    @FindBy(name = "startingYear")
    private WebElement startingYear;

    @FindBy(id = "btn-save")
    private WebElement saveButton;

    @FindBy(name = "description")
    private WebElement description;

    @FindBy(className = "et-button-bad")
    private WebElement cancelButton;


}