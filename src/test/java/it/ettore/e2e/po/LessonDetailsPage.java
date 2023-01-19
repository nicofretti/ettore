package it.ettore.e2e.po;

import it.ettore.e2e.po.professor.lessons.ProfessorLessonAddPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LessonDetailsPage extends PageObject {
    @FindBy(className = "et-name")
    private WebElement title;
    @FindBy(id = "markdown-body")
    private WebElement content;
    @FindBy(id = "btn-edit")
    private WebElement editButton;

    public LessonDetailsPage(WebDriver driver) {
        super(driver);
    }

    public Header headerComponent() {
        return new Header(driver);
    }

    public String getTitle() {
        return title.getText();
    }

    public String getContent() {
        return content.getAttribute("data-content");
    }

    public ProfessorLessonAddPage editLesson() {
        editButton.click();
        return new ProfessorLessonAddPage(driver);
    }
}

