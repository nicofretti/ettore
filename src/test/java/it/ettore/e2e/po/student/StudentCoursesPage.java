package it.ettore.e2e.po.student;

import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.PageObject;
import it.ettore.e2e.po.professor.courses.ProfessorCoursePage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage;
import lombok.EqualsAndHashCode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

public class StudentCoursesPage extends PageObject {
    public StudentCoursesPage(WebDriver driver) {
        super(driver);
    }

    public Header headerComponent() {
        return new Header(driver);
    }

    @FindBy(id = "btn-search-course")
    private WebElement searchCourseButton;

    public StudentSearchPage searchCourse() {
        searchCourseButton.click();
        return new StudentSearchPage(driver);
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

        public StudentCoursePage goTo() {
            element.findElement(By.className("et-name")).click();
            return new StudentCoursePage(driver);
        }
    }

    @FindBy(css = ".et-content > div")
    private List<WebElement> courses;

    public List<StudentCoursesPage.CourseComponent> getCourses() {
        return courses.stream().map(element -> new StudentCoursesPage.CourseComponent(driver, element)).collect(Collectors.toList());
    }
}
