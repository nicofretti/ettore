package it.ettore.e2e.po.professor.lessons;

import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.PageObject;
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

    @EqualsAndHashCode
    public static class LessonComponent {
        private WebDriver driver;
        private WebElement element;

        public LessonComponent(WebDriver driver, WebElement element) {
            this.driver = driver;
            this.element = element;
        }

        public String getTitle() {
            return element.findElement(By.className("et-name")).getText();
        }

        public String getContent() {
            return element.findElement(By.className("et-description")).getText();
        }

        public ProfessorLessonPage goTo() {
            element.findElement(By.className("et-name")).click();
            return new ProfessorLessonPage(driver);
        }
    }

    @FindBy(css = ".et-content > div")
    private List<WebElement> lessons;

    public List<LessonComponent> getCourses() {
        return lessons.stream().map(element -> new LessonComponent(driver, element)).collect(Collectors.toList());
    }

}
