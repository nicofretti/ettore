package it.ettore.e2e.po.professor.lessons;

import it.ettore.e2e.po.ErrorsComponent;
import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.PageObject;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfessorLessonAddPage extends PageObject {
    @FindBy(name = "title")
    private WebElement title;
    @FindBy(name = "description")
    private WebElement description;
    @FindBy(name = "content")
    private WebElement content;
    @FindBy(id = "btn-save")
    private WebElement saveButton;
    @FindBy(id = "btn-delete")
    private WebElement deleteButton;

    public ProfessorLessonAddPage(WebDriver driver) {
        super(driver);
    }

    public Header headerComponent() {
        return new Header(driver);
    }

    public void setTitle(String title) {
        this.title.clear();
        this.title.sendKeys(title);
    }

    public void setDescription(String description) {
        this.description.clear();
        this.description.sendKeys(description);
    }

    public void setContent(String content) {
        this.content.clear();
        this.content.sendKeys(content);
    }

    public ProfessorLessonsPage saveLesson() {
        saveButton.click();
        return new ProfessorLessonsPage(driver);
    }

    public ProfessorLessonsPage deleteLesson() {
        deleteButton.click();
        return new ProfessorLessonsPage(driver);
    }

    public boolean isSaveButtonClickable() {
        return !saveButton.getCssValue("pointer-events").equals("none");
    }

    public void clearContent() {
        content.clear();
        content.sendKeys("x" + Keys.BACK_SPACE);
    }
}