package it.ettore.e2e.po.professor.courses;

import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.PageObject;
import it.ettore.model.Course;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class ProfessorCourseAddPage extends PageObject {
    public ProfessorCourseAddPage(WebDriver driver) {
        super(driver);
    }

    public Header headerComponent() {
        return new Header(driver);
    }

    @FindBy(name = "category")
    private WebElement selectCategory;

    @FindBy(name = "name")
    private WebElement inputName;

    @FindBy(name = "startingYear")
    private WebElement selectStartingYear;

    @FindBy(id = "btn-save")
    private WebElement saveButton;

    @FindBy(name = "description")
    private WebElement inputDescription;

    @FindBy(id = "btn-delete")
    private WebElement deleteButton;

    public void setCategory(Course.Category category) {
        Select select = new Select(selectCategory);
        select.selectByValue(category.toString());
    }

    public String getCourseName() {
        return inputName.getAttribute("value");
    }

    // We need this because setCourseName("") doesn't trigger oninput on the JS side. We need to send one character and
    // then clear the input field not by calling .clear() but by sending a backspace
    public void clearName() {
        inputName.clear();
        inputName.sendKeys("x" + Keys.BACK_SPACE);
    }

    public void setCourseName(String courseName) {
        inputName.clear();
        inputName.sendKeys(courseName);
    }

    public void setStartingYear(Integer startingYear) {
        Select select = new Select(selectStartingYear);
        select.selectByValue(startingYear.toString());
    }

    public void setDescription(String description) {
        this.inputDescription.clear();
        this.inputDescription.sendKeys(description);
    }

    public boolean isSaveButtonClickable() {
        return !saveButton.getCssValue("pointer-events").equals("none");
    }

    // Redirects to the list of courses
    public ProfessorCoursesPage submitCourseAdd() {
        saveButton.click();
        return new ProfessorCoursesPage(driver);
    }

    // Redirects to the details of the course
    public ProfessorCoursePage submitCourseEdit() {
        saveButton.click();
        return new ProfessorCoursePage(driver);
    }

    public ProfessorCoursesPage submitCourseDelete() {
        deleteButton.click();
        return new ProfessorCoursesPage(driver);
    }
}
