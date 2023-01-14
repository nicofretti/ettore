package it.ettore.e2e.po.professor.courses;

import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.PageObject;
import it.ettore.e2e.po.professor.lessons.ProfessorModifyLessonPage;
import lombok.EqualsAndHashCode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class ProfessorCoursesPage extends PageObject {
    public ProfessorCoursesPage(WebDriver driver) {
        super(driver);
    }

    public Header headerComponent() {
        return new Header(driver);
    }

    @FindBy(id = "btn-new-course")
    private WebElement addNewCourse;

    public ProfessorModifyLessonPage newCourse() {
        addNewCourse.click();
        return new ProfessorModifyLessonPage(driver);
    }

    @EqualsAndHashCode
    public static class CourseComponent {
        private WebDriver driver;
        private WebElement element;

        public CourseComponent(WebDriver driver, WebElement element) {
            this.driver = driver;
            this.element = element;
        }

        public String getName() {
            return element.findElement(By.className("et-name")).getText();
        }

        public String getPeriod() {
            return element.findElement(By.className("et-period")).getText();
        }

        public String getDescription() {
            return element.findElement(By.className("et-description")).getText();
        }

        public ProfessorCoursePage goTo() {
            element.findElement(By.className("et-name")).click();
            return new ProfessorCoursePage(driver);
        }
    }

    @FindBy(css = ".et-content > div")
    private List<WebElement> courses;

    public List<CourseComponent> getCourses() {
        return courses.stream().map(element -> new CourseComponent(driver, element)).collect(Collectors.toList());
    }
}
