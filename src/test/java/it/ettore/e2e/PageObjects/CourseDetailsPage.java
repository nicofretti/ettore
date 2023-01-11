package it.ettore.e2e.PageObjects;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CourseDetailsPage extends PageObject{
    public CourseDetailsPage(WebDriver driver) {
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

    public CoursesPage clickReturnToMyCourses() {
        returnToMyCourses.click();
        return new CoursesPage(driver);
    }

    public CourseDetailsPage clickCurrentCourse() {
        currentCourse.click();
        return new CourseDetailsPage(driver);
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

