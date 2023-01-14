package it.ettore.e2e.po.professor.lessons;

import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.PageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfessorLessonPage extends PageObject {
    public ProfessorLessonPage(WebDriver driver) {
        super(driver);
    }

    public Header headerComponent() {
        return new Header(driver);
    }

    @FindBy(className = "et-name")
    private WebElement title;

    @FindBy(className = "et-description")
    private WebElement description;

    @FindBy(id = "btn-edit-lesson")
    private WebElement editButton;

    public String getName() {
        return title.getText();
    }

    public String getDescription() {
        return description.getText();
    }

//    public ProfessorEditLessonPage editLesson() {
//        editButton.click();
//        return new ProfessorEditLessonPage(driver);
//    }
}

