package it.ettore.e2e.po.professor.lessons;

import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.PageObject;
import it.ettore.e2e.po.professor.courses.ProfessorCoursePage;
import lombok.EqualsAndHashCode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

public class ProfessorLessonsPage extends PageObject {
    public ProfessorLessonsPage(WebDriver driver) {
        super(driver);
    }

    public Header headerComponent() {
        return new Header(driver);
    }

    @FindBy(id = "btn-new-lesson")
    private WebElement addNewLesson;

    // TODO Update when we have a new lesson page
    public Object newCourse() {
        addNewLesson.click();
        return null;
    }

}
