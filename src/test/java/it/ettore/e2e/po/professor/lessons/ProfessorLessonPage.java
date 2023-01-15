package it.ettore.e2e.po.professor.lessons;

import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.PageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfessorLessonPage extends PageObject {
    @FindBy(className = "et-name")
    private WebElement title;
    @FindBy(className = "markdown-body")
    private WebElement content;
    @FindBy(id = "btn-edit-lesson")
    private WebElement editButton;

    public ProfessorLessonPage(WebDriver driver) {
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

    public ProfessorModifyLessonPage editLesson() {
        editButton.click();
        return new ProfessorModifyLessonPage(driver);
    }
}

