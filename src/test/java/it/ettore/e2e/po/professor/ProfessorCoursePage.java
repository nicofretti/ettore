package it.ettore.e2e.po.professor;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.PageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfessorCoursePage extends PageObject {
    public ProfessorCoursePage(WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "fullName")
    private WebElement fullName;

    @FindBy(xpath = "//a[@href='/logout']")
    private WebElement logout;

    @FindBy(xpath = "//a[@href='/courses']")
    private WebElement returnToMyCourses;

    @FindBy(xpath = "//a[@href='']")
    private WebElement currentCourse;

    @FindBy(id = "courseName")
    private WebElement courseName;

    @FindBy(id = "coursePeriod")
    private WebElement coursePeriod;

    @FindBy(tagName = "button")
    private WebElement modifycourse;

    @FindBy(id = "courseDescription")
    private WebElement courseDescription;

    @FindBy(xpath = "//a[@href='lessons/']")
    private WebElement courseLessonsLink;

    @FindBy(xpath = "//a[@href='quiz/']")
    private WebElement courseQuizLink;

    @FindBy(xpath = "//a[@href='students/']")
    private WebElement courseStudentsLink;

    public String getFullName() {
        return fullName.getText();
    }

    public LoginPage clickLogout() {
        logout.click();
        return new LoginPage(driver);
    }

    public ProfessorCoursesPage clickReturnToMyCourses() {
        returnToMyCourses.click();
        return new ProfessorCoursesPage(driver);
    }

    public ProfessorCoursePage clickCurrentCourse() {
        currentCourse.click();
        return new ProfessorCoursePage(driver);
    }

    public String getCourseName() {
        return courseName.getText();
    }

    public String getCoursePeriod() {
        return coursePeriod.getText();
    }

    /*public ModifyCoursePage clickModifyCourse() {
        modifycourse.click();
        return new ModifyCoursePage(driver);
    }*/

    public String getCourseDescription() {
        return courseDescription.getText();
    }

    /*public LessonsPage clickCourseLessonsLink() {
        courseLessonsLink.click();
        return new LessonsPage(driver);
    }*/

    /*public QuizPage clickCourseQuizLink() {
        courseQuizLink.click();
        return new QuizPage(driver);
    }*/

    /*public StudentsPage clickCourseStudentsLink() {
        courseStudentsLink.click();
        return new StudentsPage(driver);
    }*/
}

