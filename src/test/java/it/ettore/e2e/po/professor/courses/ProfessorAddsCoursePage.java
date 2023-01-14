package it.ettore.e2e.po.professor.courses;

import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.PageObject;
import org.openqa.selenium.By;
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

    //findBy using category value
    public void selectCategory(String category) {

        this.category.findElement(By.xpath(String.format("//option[@value='%s']", category))).click();
    }

    public void setCourseName(String courseName) {

        this.courseName.clear();
        this.courseName.sendKeys(courseName);
    }

    public void setStartingYear(String startingYear) {
        this.startingYear.findElement(By.xpath(String.format("//option[@value='%s']", startingYear))).click();
    }

    public void setDescription(String description) {
        this.description.clear();
        this.description.sendKeys(description);
    }

    public ProfessorCoursesPage save() {
        saveButton.click();
        return new ProfessorCoursesPage(driver);
    }

    public ProfessorCoursesPage cancel() {
        cancelButton.click();
        return new ProfessorCoursesPage(driver);
    }
}
