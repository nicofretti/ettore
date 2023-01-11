package it.ettore.e2e.PageObjects;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CourseDetailsPage extends PageObject{
    public CourseDetailsPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//body/div[1]/p")
    private WebElement fullName;

    @FindBy(xpath = "//body/div[1]/a")
    private WebElement logout;

    @FindBy(xpath = "//body/div[2]/div/a[1]")
    private WebElement returnToMyCourses;

    @FindBy(xpath = "//body/div[2]/div/a[2]")
    private WebElement currentCourse;

    @FindBy(xpath = "//body/div[3]/div/div/div/p[1]")
    private WebElement courseName;

    @FindBy(xpath = "//body/div[3]/div/div/div/p[2]")
    private WebElement coursePeriod;

    @FindBy(xpath = "//body/div[3]/div/div/button")
    private WebElement modifycourse;

    @FindBy(xpath = "//body/div[3]/div/p")
    private WebElement courseDescription;

    @FindBy(xpath = "//body/div[3]/div[2]/a[1]")
    private WebElement courseLessonsLink;

    @FindBy(xpath = "//body/div[3]/div[2]/a[2]")
    private WebElement courseQuizLink;

    @FindBy(xpath = "//body/div[3]/a")
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

