package it.ettore.e2e.po.professor.lessons;

import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.PageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfessorModifyLessonPage extends PageObject {
    @FindBy(name = "title")
    private WebElement title;
    @FindBy(name = "description")
    private WebElement description;
    @FindBy(name = "content")
    private WebElement content;
    @FindBy(id = "btn-save-lesson")
    private WebElement saveButton;
    @FindBy(id = "btn-cancel-lesson")
    private WebElement cancelButton;

    public ProfessorModifyLessonPage(WebDriver driver) {
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
        this.content.click();
    }

    public ProfessorLessonsPage saveLesson() {
        saveButton.click();
        return new ProfessorLessonsPage(driver);
    }

    public ProfessorLessonsPage cancelLesson() {
        cancelButton.click();
        return new ProfessorLessonsPage(driver);
    }
}