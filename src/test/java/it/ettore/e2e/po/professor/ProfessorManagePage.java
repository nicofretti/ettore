package it.ettore.e2e.po.professor;

import it.ettore.e2e.po.Header;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.PageObject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class ProfessorManagePage extends PageObject {
    public ProfessorManagePage(WebDriver driver) {
        super(driver);
    }

    public Header headerComponent() {
        return new Header(driver);
    }

    @EqualsAndHashCode
    public static class StudentRequestingComponent {
        private WebDriver driver;
        private WebElement element;

        public StudentRequestingComponent(WebDriver driver, WebElement element) {
            this.driver = driver;
            this.element = element;
        }

        public String getFullName() {
            return element.findElement(By.tagName("span")).getText();
        }

        public ProfessorManagePage approve() {
            element.findElement(By.xpath("//button[1]")).click();
            return new ProfessorManagePage(driver);
        }

        public ProfessorManagePage reject() {
            element.findElement(By.xpath("//button[2]")).click();
            return new ProfessorManagePage(driver);
        }
    }

    @EqualsAndHashCode
    public static class StudentJoinedComponent {
        private WebDriver driver;
        private WebElement element;

        public StudentJoinedComponent(WebDriver driver, WebElement element) {
            this.driver = driver;
            this.element = element;
        }

        public String getFullName() {
            return element.findElement(By.tagName("span")).getText();
        }

        public ProfessorManagePage remove() {
            element.findElement(By.xpath("//button")).click();
            return new ProfessorManagePage(driver);
        }
    }

    @FindBy(css = "#list-students-requesting > div")
    private List<WebElement> studentsRequesting;

    @FindBy(css = "#list-students-joined > div")
    private List<WebElement> studentsJoined;

    public List<StudentRequestingComponent> getStudentsRequesting() {
        return studentsRequesting.stream().map(element -> new StudentRequestingComponent(driver, element)).collect(Collectors.toList());
    }

    public List<StudentJoinedComponent> getStudentsJoined() {
        return studentsJoined.stream().map(element -> new StudentJoinedComponent(driver, element)).collect(Collectors.toList());
    }
}
